package com.github.winteryoung.yanwte.internals.bytecode

import com.github.winteryoung.yanwte.internals.utils.ByteArrayClassLoader
import java.util.*

/**
 * Both extension point proxy and extension execution proxy have the same instance
 * creation process. So this method creates any one of them.
 */
internal fun <T> loadExtensionPointDelegateInstance(
        name: String,
        bytes: ByteArray,
        constructorArg: Any
): T {
    val typeDefinitions = HashMap<String, ByteArray>().apply {
        this[name] = bytes
    }
    val classloader = ByteArrayClassLoader(getParentClassLoader(constructorArg), typeDefinitions)
    val cls = classloader.loadClass(name)

    cls.declaredConstructors[0].let { it ->
        @Suppress("UNCHECKED_CAST")
        return it.newInstance(constructorArg) as T
    }
}

private fun getParentClassLoader(constructorArg: Any): ClassLoader {
    Thread.currentThread().contextClassLoader?.let {
        return it
    }
    return constructorArg.javaClass.classLoader
}