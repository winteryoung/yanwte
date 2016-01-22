package com.github.winteryoung.yanwte

/**
 * @author Winter Young
 * @since 2016/1/18
 */
class YanwteException(
        msg: String? = null,
        cause: Throwable? = null
) : RuntimeException(msg, cause) {
    companion object {
        private val serialVersionUid: Long = 1
    }
}