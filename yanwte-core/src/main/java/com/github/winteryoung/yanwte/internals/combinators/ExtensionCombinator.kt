package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.internals.YanwteExtension

/**
 * The node that executes extensions. This kind of nodes must be leaves.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
internal class ExtensionCombinator constructor(
        extensionPointName: String,
        override val extension: YanwteExtension
) : ExtensionAwareCombinator(extensionPointName, "ext") {
    override fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput {
        return extension(input)
    }
}