package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.YanwteException
import com.github.winteryoung.yanwte.YanwtePlugin
import org.springframework.context.ApplicationContext

/**
 * This plugin implementation supports integrating Spring.
 *
 * @author Winter Young
 * @since 2016/1/23
 */
internal class SpringPlugin(val applicationContext: ApplicationContext) : YanwtePlugin {
    override fun getExtensionByName(extensionName: String): Any? {
        val extensionClass = applicationContext.classLoader.let { cl ->
            cl.loadClass(extensionName)
        }
        val extensions = applicationContext.getBeansOfType(extensionClass).values.toList()
        if (extensions.size != 1) {
            throw YanwteException("Cannot find a unique bean with type $extensionName," +
                    " got ${extensions.size} beans")
        }
        return extensions[0]
    }
}