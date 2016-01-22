package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.YanwtePlugin

/**
 * This Yanwte plugin implementation does single classloader loading, and
 * the objects like extension points and extensions are created by this
 * plugin. It's suitable for library jars, which no multiple classloaders are
 * needed, and objects creations are not managed by external contains like Spring.
 *
 * @author Winter Young
 * @since 2016/1/21
 */
internal class DefaultYanwtePlugin : YanwtePlugin {
    override fun getExtensionByName(extensionName: String): Any? {
        return createInstance(extensionName)
    }

    private fun createInstance(name: String): Any {
        Thread.currentThread().contextClassLoader?.let { cccl ->
            val c: Class<*>
            try {
                c = cccl.loadClass(name)
                return c.newInstance()
            } catch (e: ClassNotFoundException) {
                // try next
            }
        }

        val cl = DefaultYanwtePlugin::class.java.classLoader
        val c = cl.loadClass(name)
        return c.newInstance()
    }
}