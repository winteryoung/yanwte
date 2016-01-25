package com.github.winteryoung.yanwte

/**
 * Data extension initializer. To let it work, client code shall place a class called
 * `DataExtensionInitializer` under the extension space, and let it implement this interface.
 *
 * @author Winter Young
 * @since 2016/1/25
 */
interface YanwteDataExtensionInitializer {
    /**
     * Initializes the data extension for the given data extension point.
     * Typically, client code should do `instanceof` check against [dataExtensionPoint].
     * If using Kotlin, use `when` on [dataExtensionPoint.javaClass].
     * So that you know what kind of data extension to create.
     */
    fun initialize(dataExtensionPoint: DataExtensionPoint): Any?
}