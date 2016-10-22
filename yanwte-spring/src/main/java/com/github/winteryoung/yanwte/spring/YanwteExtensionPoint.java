package com.github.winteryoung.yanwte.spring;

import java.lang.annotation.*;

/**
 * 被标记的接口可以被 yanwte-spring 自动扫描到，从而可以被自动注入。
 *
 * @author fanshen
 * @since 2016/10/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YanwteExtensionPoint {
}
