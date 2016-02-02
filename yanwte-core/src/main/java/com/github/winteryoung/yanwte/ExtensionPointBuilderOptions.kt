package com.github.winteryoung.yanwte

/**
 * Options for [ExtensionPointBuilder].
 *
 * @author Winter Young
 */
data class ExtensionPointBuilderOptions(
        /**
         * Throws an exception if no extension can be found for the given name or class.
         * The default is true, so it can fail-fast.
         */
        var failOnExtensionNotFound: Boolean = true
)