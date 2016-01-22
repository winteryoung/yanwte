package com.github.winteryoung.yanwte

/**
 * Extension point return value.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
data class ExtensionPointOutput(
        /**
         * Return value.
         */
        val returnValue: Any?
) {
    companion object {
        val empty = ExtensionPointOutput(null)
    }
}