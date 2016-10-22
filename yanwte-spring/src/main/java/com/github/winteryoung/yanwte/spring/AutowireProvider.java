package com.github.winteryoung.yanwte.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * Auto wire extension point provider instance to fields marked by this annotation.
 * The corresponding extension point interface must be marked with {@link YanwteExtensionPoint}.
 *
 * @author Winter Young
 * @since 2016/10/22
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
@Lazy
public @interface AutowireProvider {
}
