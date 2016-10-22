package com.github.winteryoung.yanwte.spring.internals;

import com.github.winteryoung.yanwte.YanwteContainer;

/**
 * 用来在 spring 中创建 extension point provider。
 *
 * @author fanshen
 * @since 2016/10/22
 */
public class ExtensionPointProviderFactoryBean {
    /**
     * 框架内部使用。
     */
    public Object createExtensionPointProvider(Class<?> extensionPointClass) {
        return YanwteContainer.getExtensionPointByClass(extensionPointClass);
    }
}
