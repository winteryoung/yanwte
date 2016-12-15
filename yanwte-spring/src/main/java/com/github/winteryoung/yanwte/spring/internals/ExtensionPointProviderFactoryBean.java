package com.github.winteryoung.yanwte.spring.internals;

import com.github.winteryoung.yanwte.YanwteContainer;
import com.github.winteryoung.yanwte.YanwteOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory bean for extension point providers.
 *
 * @author Winter Young
 * @since 2016/10/22
 */
@SuppressWarnings("WeakerAccess")
public class ExtensionPointProviderFactoryBean {
    private Logger log = LoggerFactory.getLogger(ExtensionPointProviderFactoryBean.class);

    /**
     * Factory method that creates the provider instance for the specified
     * extension point interface.
     */
    public Object createExtensionPointProvider(Class<?> extensionPointClass) {
        if (YanwteOptions.getLogExtensionsBuild() && log.isWarnEnabled()) {
            log.warn("createExtensionPointProvider, extensionPointInterface: " + extensionPointClass);
        }
        return YanwteContainer.getExtensionPointByClass(extensionPointClass);
    }
}
