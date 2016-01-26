package com.github.winteryoung.yanwte.internals.utils

internal fun <T> T?.onNull(action: () -> Unit): T? {
    if (this == null) {
        action()
    }
    return this
}
