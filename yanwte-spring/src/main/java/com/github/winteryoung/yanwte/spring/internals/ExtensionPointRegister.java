package com.github.winteryoung.yanwte.spring.internals;

import com.github.winteryoung.yanwte.YanwteContainer;
import com.github.winteryoung.yanwte.YanwteException;
import com.github.winteryoung.yanwte.spring.YanwteExtensionPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring 初始化后自动注册扩展点。
 *
 * @author fanshen
 * @since 2016/10/22
 */
@Component
public class ExtensionPointRegister implements BeanDefinitionRegistryPostProcessor {
    private ClassLoader ccl = Thread.currentThread().getContextClassLoader();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Class<?>[] classes;
        try {
            classes = ReflectionUtils.getClasses("", getClass().getClassLoader());
        } catch (IOException e) {
            throw new YanwteException(e.getMessage(), e);
        }

        List<Class<?>> extensionPointClasses = Arrays.stream(classes).filter((Class cls) -> {
            Annotation annotation = cls.getAnnotation(YanwteExtensionPoint.class);
            return annotation != null;
        }).collect(Collectors.toList());

        for (Class<?> extensionPointClass : extensionPointClasses) {
            Object provider = YanwteContainer.getExtensionPointByClass(extensionPointClass);
            beanFactory.registerSingleton(extensionPointClass.getSimpleName() + "Provider", provider);
        }
    }
}
