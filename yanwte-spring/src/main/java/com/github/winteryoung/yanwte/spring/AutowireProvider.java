package com.github.winteryoung.yanwte.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * 自动注入 extension point provider。
 *
 * @author fanshen
 * @since 2016/10/22
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
@Lazy
public @interface AutowireProvider {
}
