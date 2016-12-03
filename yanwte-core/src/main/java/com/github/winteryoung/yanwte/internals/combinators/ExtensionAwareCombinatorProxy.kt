package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.internals.YanwteExtension

/**
 * @author Winter Young
 * @since 2016/12/3
 */
internal abstract class ExtensionAwareCombinatorProxy(
        val target: ExtensionAwareCombinator,
        extensionPointName: String,
        name: String
) : ExtensionAwareCombinator(extensionPointName, name) {
    override fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput {
        return target.invoke(input)
    }

    override val extension: YanwteExtension?
        get() = target.extension
}