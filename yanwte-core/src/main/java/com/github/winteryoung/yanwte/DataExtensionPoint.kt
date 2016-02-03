package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.YanwteRuntime
import com.github.winteryoung.yanwte.internals.utils.onNull

/**
 * A domain class implementing data extension point means it is able to extend its data.
 * Compared to extension point, you can think of that as a kind of behavioral
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
        return YanwteRuntime.currentRunningExtension!!.run {
            getDataExtension<T>(extensionSpaceName)
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

    fun <T> getDataExtension(extSpace: YanwteExtensionSpace): T? {
        return getDataExtension(extSpace.name)
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

    return YanwteContainer.getDataExtInitializer(extSpaceName)?.let {
        it.initialize(this)?.let {
            YanwteContainer.cacheDataExtension(this, extSpaceName, it)
            it as T
        }
    }
}
