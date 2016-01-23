package com.github.winteryoung.yanwte.internals.utils

import java.util.*

/**
 * Package name keyed map finds an entry if it has the given package name registered in it,
 * or the ancestor package of the given package name registered in it.
 *
 * @author Winter Young
 * @since 2016/1/21
 */
internal class PackageNamedKeyMap<T> {
    private val map = TreeMap<String, T>()

    /**
     * Returns the value if this map has the given package name registered in it,
     * or the ancestor package of the given package name registered in it.
     */
    operator fun get(packageName: String): T? {
        if (packageName.isEmpty()) {
            return null
        }

        map[packageName]?.let {
            return it
        }

        return get(packageName.substringBeforeLast(".", missingDelimiterValue = ""))
    }

    /**
     * Sets the value associated with the given package name.
     */
    operator fun set(packageName: String, value: T) {
        map[packageName] = value
    }

    /**
     * If this contains the given key.
     */
    fun containsKey(key: String) = map.containsKey(key)

    /**
     * Clear this map.
     */
    fun clear() {
        map.clear()
    }
}