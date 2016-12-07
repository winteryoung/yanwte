package com.github.winteryoung.yanwte.spring.internals

import com.github.winteryoung.yanwte.YanwteException
import com.github.winteryoung.yanwte.YanwteOptions
import com.github.winteryoung.yanwte.internals.utils.ReflectionUtils
import com.github.winteryoung.yanwte.spring.YanwteExtensionPoint
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * Auto register extension point providers after Spring initialization.

 * @author Winter Young
 * *
 * @since 2016/10/22
 */
@Component
class ExtensionPointRegister : BeanFactoryPostProcessor {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(BeansException::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val defaultListableBeanFactory = beanFactory as DefaultListableBeanFactory
        val classes: Array<Class<*>>
        val classLoader = Thread.currentThread().contextClassLoader

        try {
            classes = ReflectionUtils.getClasses("", classLoader)
        } catch (e: IOException) {
            throw YanwteException(e.message, e)
        }

        val extensionPointClasses = classes.filter { cls: Class<*> ->
                cls.getAnnotation(YanwteExtensionPoint::class.java) != null
        }

        beanFactory.registerSingleton("extensionPointProviderFactoryBean", ExtensionPointProviderFactoryBean())

        for (extensionPointClass in extensionPointClasses) {
            val providerClass = getProviderClass(extensionPointClass, classLoader)
            if (YanwteOptions.logExtensionsBuild && log.isWarnEnabled) {
                log.warn("postProcessBeanFactory, extensionPointInterface: " + extensionPointClass
                        + ", provider: " + providerClass)
            }
            val beanDefinition = buildBeanDefinition(extensionPointClass, providerClass)
            val uncapitalized = StringUtils.uncapitalize(extensionPointClass.simpleName)

            defaultListableBeanFactory.registerBeanDefinition(uncapitalized + "Provider", beanDefinition)
            defaultListableBeanFactory.clearMetadataCache()
        }
    }

    private fun buildBeanDefinition(extensionPointClass: Class<*>, providerClass: Class<*>): BeanDefinition {
        val beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(providerClass)
                .setLazyInit(true)
                .rawBeanDefinition as RootBeanDefinition
        beanDefinition.factoryBeanName = "extensionPointProviderFactoryBean"
        beanDefinition.factoryMethodName = "createExtensionPointProvider"
        val args = ConstructorArgumentValues()
        args.addGenericArgumentValue(extensionPointClass)
        beanDefinition.constructorArgumentValues = args
        beanDefinition.targetType = extensionPointClass
        return beanDefinition
    }

    private fun getProviderClass(extensionPointClass: Class<*>, classLoader: ClassLoader): Class<*> {
        try {
            return classLoader.loadClass(extensionPointClass.name + "Provider")
        } catch (e: ClassNotFoundException) {
            throw YanwteException(e.message, e)
        }
    }
}
