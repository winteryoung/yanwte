package com.github.winteryoung.yanwte.spring;

import java.lang.annotation.*;

/**
 * Those extension point interfaces marked by this annotation will be automatically detected
 * by yanwte-spring. This is required in conjunction with {@link AutowireProvider}.
 *
 * @author Winter Young
 * @since 2016/10/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YanwteExtensionPoint {
}
