package com.github.winteryoung.yanwte.spring.internals;

import com.github.winteryoung.yanwte.YanwteContainer;

/**
 * Factory bean for extension point providers.
 *
 * @author Winter Young
 * @since 2016/10/22
 */
@SuppressWarnings("WeakerAccess")
public class ExtensionPointProviderFactoryBean {
    /**
     * Factory method that creates the provider instance for the specified
     * extension point interface.
     */
    public Object createExtensionPointProvider(Class<?> extensionPointClass) {
        return YanwteContainer.getExtensionPointByClass(extensionPointClass);
    }
}
