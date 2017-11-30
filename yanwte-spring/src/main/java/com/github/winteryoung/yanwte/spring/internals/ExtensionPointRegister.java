package com.github.winteryoung.yanwte.spring.internals;

import com.github.winteryoung.yanwte.YanwteException;
import com.github.winteryoung.yanwte.YanwteOptions;
import com.github.winteryoung.yanwte.spring.YanwteExtensionPoint;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * Auto register extension point providers after Spring initialization.
 *
 * @author Winter Young
 * @since 2016/10/22
 */
@SuppressWarnings("unused")
@Component
public class ExtensionPointRegister implements BeanFactoryPostProcessor {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) beanFactory;
        List<Class<?>> classes;
        try {
            classes =
                    ClassPath.from(Thread.currentThread().getContextClassLoader())
                            .getAllClasses()
                            .stream()
                            .map(this::loadClass)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
        } catch (IOException e) {
            throw new YanwteException(e.getMessage(), e);
        }

        if (YanwteOptions.getLogExtensionsBuild() && log.isWarnEnabled()) {
            log.warn(
                    "Yanwte scanned classes:\n"
                            + classes.stream()
                                    .map(Class::getName)
                                    .collect(Collectors.joining("\n")));
        }

        List<Class<?>> extensionPointClasses =
                classes.stream()
                        .filter(
                                (Class<?> cls) -> {
                                    Annotation annotation = null;
                                    try {
                                        annotation = cls.getAnnotation(YanwteExtensionPoint.class);
                                    } catch (Throwable ignored) {
                                    }
                                    return annotation != null;
                                })
                        .collect(Collectors.toList());

        beanFactory.registerSingleton(
                "extensionPointProviderFactoryBean", new ExtensionPointProviderFactoryBean());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (Class<?> extensionPointClass : extensionPointClasses) {
            Class<?> providerClass = getProviderClass(extensionPointClass, classLoader);
            if (YanwteOptions.getLogExtensionsBuild() && log.isWarnEnabled()) {
                log.warn(
                        "Yanwte builds provider <"
                                + providerClass
                                + "> for interface <"
                                + extensionPointClass
                                + ">");
            }
            BeanDefinition beanDefinition = buildBeanDefinition(extensionPointClass, providerClass);
            String extensionPointClassName = extensionPointClass.getSimpleName();
            String uncapitalized =
                    extensionPointClassName.substring(0, 1).toLowerCase()
                            + extensionPointClassName.substring(1);

            defaultListableBeanFactory.registerBeanDefinition(
                    uncapitalized + "Provider", beanDefinition);
            defaultListableBeanFactory.clearMetadataCache();
        }
    }

    @Nullable
    private Class<?> loadClass(ClassPath.ClassInfo classInfo) {
        try {
            return classInfo.load();
        } catch (Throwable ex) {
            if (YanwteOptions.getLogExtensionsBuild() && log.isWarnEnabled()) {
                log.warn("Yanwte cannot load class: " + classInfo.getName(), ex);
            }
            return null;
        }
    }

    @NotNull
    private BeanDefinition buildBeanDefinition(
            Class<?> extensionPointClass, Class<?> providerClass) {
        RootBeanDefinition beanDefinition =
                (RootBeanDefinition)
                        BeanDefinitionBuilder.rootBeanDefinition(providerClass)
                                .setLazyInit(true)
                                .getRawBeanDefinition();
        beanDefinition.setFactoryBeanName("extensionPointProviderFactoryBean");
        beanDefinition.setFactoryMethodName("createExtensionPointProvider");
        ConstructorArgumentValues args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(extensionPointClass);
        beanDefinition.setConstructorArgumentValues(args);
        beanDefinition.setTargetType(extensionPointClass);
        return beanDefinition;
    }

    private Class<?> getProviderClass(Class<?> extensionPointClass, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(extensionPointClass.getName() + "Provider");
        } catch (ClassNotFoundException e) {
            throw new YanwteException(e.getMessage(), e);
        }
    }
}
