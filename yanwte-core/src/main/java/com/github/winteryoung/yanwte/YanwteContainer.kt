package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.bytecode.generateExtensionPointProxy
import java.util.concurrent.ConcurrentHashMap

/**
 * YanwteContainer registers extensions, extension points, namespaces, and keep track of the relations between them.
 *
 * @author Winter Young
 * @since 2016/1/19
 */
object YanwteContainer {
    /**
     * Extension name to instance map.
     */
    private val nameToExtension = ConcurrentHashMap<String, YanwteExtension>()

    /**
     * Extension point name to instance map.
     */
    private val nameToExtPoint = ConcurrentHashMap<String, ExtensionPoint>()

    /**
     * Extension point to its corresponding POJO proxy that delegates the SAM call to it.
     */
    private val extPointToProxy = ConcurrentHashMap<ExtensionPoint, Any>()

    /**
     * Clear this container.
     */
    internal fun clear() {
        nameToExtension.clear()
        nameToExtPoint.clear()
    }

    /**
     * Register extension.
     */
    private fun registerYanwteExtension(extension: YanwteExtension) {
        nameToExtension[extension.name] = extension
    }

    /**
     * Returns the extension with the given name, and null if no match.
     */
    internal fun getExtensionByName(name: String): YanwteExtension? {
        return nameToExtension[name]
    }

    /**
     * Register the given extension point. This process registers all the dependent extensions
     * of the given extension point.
     */
    fun registerExtensionPoint(extensionPoint: ExtensionPoint) {
        nameToExtPoint[extensionPoint.name] = extensionPoint

        extensionPoint.combinator.collectDependentExtensions().forEach {
            registerYanwteExtension(it)
        }
    }

    /**
     * Get the extension point matching the given name.
     */
    internal fun getExtensionPointByName(name: String): ExtensionPoint? {
        return nameToExtPoint[name]
    }


    /**
     * Returns the POJO extension point instance by the given POJO extension point
     * interface class.
     */
    fun <T : Any> getExtensionPointByClass(extensionPointInterfaceClass: Class<T>): T? {
        val extPointName = extensionPointInterfaceClass.name
        val extPoint = nameToExtPoint[extPointName]
                ?: throw YanwteException("Cannot find extension point with name $extPointName")

        extPointToProxy[extPoint]?.let {
            @Suppress("UNCHECKED_CAST")
            return it as T
        }

        val proxy = generateExtensionPointProxy(extPoint, extensionPointInterfaceClass)
        extPointToProxy[extPoint] = proxy

        return proxy
    }
}