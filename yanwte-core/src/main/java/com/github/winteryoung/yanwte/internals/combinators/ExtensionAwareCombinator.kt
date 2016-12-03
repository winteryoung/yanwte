package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.internals.YanwteExtension

/**
 * @author Winter Young
 * @since 2016/12/3
 */
internal abstract class ExtensionAwareCombinator(
        extensionPointName: String,
        name: String
) : Combinator(extensionPointName, emptyList(), name) {
    abstract val extension: YanwteExtension?
}