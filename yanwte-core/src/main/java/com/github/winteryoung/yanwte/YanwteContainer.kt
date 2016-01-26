package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.bytecode.generateExtensionPointDelegate
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
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
     * Data extension point to data extensions map. The value is the second level map.
     * The second level map's key is the extension space name. The final value is
     * the data extension to the data extension point.
     */
    private val dataExtensionPointToExtensions: Cache<Any, ConcurrentHashMap<String, Any>> =
            CacheBuilder.newBuilder().weakKeys().build()

    /**
     * Extension space name to data initializer map.
     */
    private val extSpaceNameToDataExtInitializer: ConcurrentHashMap<String, YanwteDataExtensionInitializer> =
            ConcurrentHashMap()

    /**
     * Extension space name to instance map.
     */
    private val nameToExtSpace = ConcurrentHashMap<String, YanwteExtensionSpace>()

    /**
     * Clear this container.
     */
    internal fun clear() {
        nameToExtension.clear()
        nameToExtPoint.clear()
        dataExtensionPointToExtensions.cleanUp()
        extSpaceNameToDataExtInitializer.clear()
        nameToExtSpace.clear()
    }

    internal fun registerDataExtInitializer(extSpaceName: String, dataExtInitializer: YanwteDataExtensionInitializer) {
        extSpaceNameToDataExtInitializer[extSpaceName] = dataExtInitializer
    }

    internal fun getDataExtInitializer(extSpaceName: String): YanwteDataExtensionInitializer? {
        return extSpaceNameToDataExtInitializer[extSpaceName]
    }

    internal fun registerDataExtension(dataExtensionPoint: Any, extensionSpaceName: String, dataExtension: Any) {
        dataExtensionPointToExtensions.get(dataExtensionPoint) {
            ConcurrentHashMap()
        }.let { secMap ->
            secMap[extensionSpaceName] = dataExtension
        }
    }

    internal fun getDataExtension(dataExtensionPoint: Any, extensionSpaceName: String): Any? {
        return dataExtensionPointToExtensions.get(dataExtensionPoint) {
            ConcurrentHashMap()
        }.let { secMap ->
            secMap[extensionSpaceName]
        }
    }

    /**
     * Register extension.
     */
    private fun registerYanwteExtension(extension: YanwteExtension) {
        nameToExtension[extension.name] = extension
        registerExtensionSpace(extension.extensionSpaceName, extension.extensionSpace)
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
    internal fun registerExtensionPoint(extensionPoint: ExtensionPoint) {
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
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtensionPointByClass(extensionPointInterfaceClass: Class<T>): T? {
        val extPointName = extensionPointInterfaceClass.name
        val extPoint = nameToExtPoint[extPointName]
                ?: throw YanwteException("Cannot find extension point with name $extPointName")

        extPoint.pojoExtensionPoint?.let {
            return it as T
        }

        generateExtensionPointDelegate(extPoint, extensionPointInterfaceClass).let { delegate ->
            extPoint.pojoExtensionPoint = delegate
            return delegate
        }
    }

    /**
     * Returns the extension space instance of the given name.
     */
    internal fun getExtensionSpaceByName(name: String): YanwteExtensionSpace? {
        return nameToExtSpace[name]
    }

    /**
     * Register the extension space with the given name.
     */
    internal fun registerExtensionSpace(name: String, space: YanwteExtensionSpace) {
        nameToExtSpace[name] = space
    }
}