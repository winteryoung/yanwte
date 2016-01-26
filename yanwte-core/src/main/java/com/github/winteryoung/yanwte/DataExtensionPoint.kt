package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.YanwteRuntime
import com.github.winteryoung.yanwte.internals.utils.onNull

/**
 * A class implementing data extension point means it is able to extend its data.
 * Compared to `ExtensionPoint`, you can think of that as a kind of behavioral
 * extension point. This is focused on data.
 *
 * @author Winter Young
 * @since 2016/1/25
 */
interface DataExtensionPoint {
    /**
     * Returns the data extension bound to the current extension space.
     * The current extension space is calculated from the current running
     * extension of the current thread.
     */
    fun <T> getDataExtension(): T? {
        return YanwteRuntime.currentRunningExtension!!.let { extension ->
            extension.extensionSpaceName.let { extSpaceName ->
                getDataExtension<T>(extSpaceName, extension)
            }
        }
    }

    /**
     * Returns the data extension bound to the given extension space.
     * The extension space corresponding to [extSpaceName] must have authorized
     * this extension space. Otherwise, an [YanwteException] will be thrown.
     */
    fun <T> getDataExtension(extSpaceName: String): T? {
        return YanwteRuntime.currentRunningExtension!!.let { extension ->
            getDataExtension<T>(extSpaceName, extension)
        }
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T> DataExtensionPoint.getDataExtension(extSpaceName: String, extension: YanwteExtension): T? {
    YanwteContainer.getExtensionSpaceByName(extSpaceName).onNull {
        throw YanwteException("Extension space name [$extSpaceName] not registered")
    }?.let { srcSpace ->
        extension.extensionSpace.let { targetSpace ->
            if (srcSpace.isAuthorizedTo(targetSpace.name).not()) {
                throw YanwteException("${targetSpace.name} has not been authorized by ${srcSpace.name}")
            }
        }
    }

    YanwteContainer.getDataExtension(this, extSpaceName)?.let {
        return it as T
    }

    return initDataExt(extSpaceName, extension.pojoExtension!!, this)?.let {
        YanwteContainer.registerDataExtension(this, extSpaceName, it)
        it as T
    }
}

@Suppress("UNCHECKED_CAST")
private fun initDataExt(extSpaceName: String, extension: Any, dataExtensionPoint: DataExtensionPoint): Any? {
    return getDataExtInitializer(extSpaceName) { extSpaceName ->
        extension.javaClass.classLoader.let {
            try {
                it.loadClass("$extSpaceName.DataExtensionInitializer") as Class<YanwteDataExtensionInitializer>
            } catch (e: ClassNotFoundException) {
                null
            }
        }
    }.let { dataExtInitializer ->
        dataExtInitializer.initialize(dataExtensionPoint)
    }
}

internal fun getDataExtInitializer(
        extSpaceName: String,
        initializerClassLoader: (String) -> Class<YanwteDataExtensionInitializer>?
): YanwteDataExtensionInitializer {
    YanwteContainer.getDataExtInitializer(extSpaceName)?.let {
        return it
    }

    initializerClassLoader(extSpaceName)?.let {
        it.newInstance().let {
            YanwteContainer.registerDataExtInitializer(extSpaceName, it)
        }
        return YanwteContainer.getDataExtInitializer(extSpaceName)!!
    }

    return EmptyDataExtensionInitializer.let {
        YanwteContainer.registerDataExtInitializer(extSpaceName, it)
        it
    }
}