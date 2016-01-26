package com.github.winteryoung.yanwte.internals

/**
 * The runtime information of Yanwte.
 *
 * @author Winter Young
 * @since 2016/1/26
 */
internal object YanwteRuntime {
    private val currentRunningExtensionThreadLocal = ThreadLocal<YanwteExtension>()

    /**
     * Get or set the current running extension of the current thread.
     */
    var currentRunningExtension: YanwteExtension?
        get() {
            return currentRunningExtensionThreadLocal.get()
        }
        set(value) {
            currentRunningExtensionThreadLocal.set(value)
        }
}