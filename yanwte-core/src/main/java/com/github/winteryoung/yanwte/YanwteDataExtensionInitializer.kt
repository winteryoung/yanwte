package com.github.winteryoung.yanwte

/**
 * Data extension initializer. To let it work, client code shall place a class called
 * `DataExtensionInitializer` under the extension space, and let it extends this class.
 *
 * @author Winter Young
 * @since 2016/1/25
 */
abstract class YanwteDataExtensionInitializer {
    /**
     * Initializes the data extension for the given data extension point.
     * Typically, client code should do `instanceof` check against [dataExtensionPoint].
     * If using Kotlin, use `when` on [dataExtensionPoint.javaClass].
     * So that you know what kind of data extension to create.
     */
    abstract fun initialize(dataExtensionPoint: DataExtensionPoint): Any?
}

/**
 * This empty data extension initializer serves as a place holder for those
 * extension spaces who haven't defined a data extension initializer.
 */
internal object EmptyDataExtensionInitializer : YanwteDataExtensionInitializer() {
    override fun initialize(dataExtensionPoint: DataExtensionPoint): Any? {
        return null
    }
}