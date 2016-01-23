package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.YanwtePlugin

/**
 * This Yanwte plugin implementation does single classloader loading, and
 * the objects like extension points and extensions are created by this
 * plugin. It's suitable for library jars, which no multiple classloaders are
 * needed, and objects creations are not managed by external containers like Spring.
 *
 * @author Winter Young
 * @since 2016/1/21
 */
internal class DefaultYanwtePlugin : YanwtePlugin {
    override fun getExtensionByName(extensionName: String): Any? {
        return createInstance(extensionName)
    }

    private fun createInstance(name: String): Any {
        Thread.currentThread().contextClassLoader?.let { ccl ->
            val c: Class<*>
            try {
                c = ccl.loadClass(name)
                return c.newInstance()
            } catch (e: ClassNotFoundException) {
                // try next
            }
        }

        val cl = this.javaClass.classLoader
        val c = cl.loadClass(name)
        return c.newInstance()
    }
}