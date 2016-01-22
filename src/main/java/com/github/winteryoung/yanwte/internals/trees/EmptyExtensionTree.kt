package com.github.winteryoung.yanwte.internals.trees

import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.ExtensionTree

/**
 * In case anyone want to write an empty provide, this node can help.
 * It does nothing but returns null on any input.
 *
 * @author Winter Young
 * @since 2016/1/19
 */
internal class EmptyExtensionTree(
        extensionPointName: String
) : ExtensionTree(extensionPointName, emptyList(), "empty") {
    override fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput {
        return ExtensionPointOutput.empty
    }
}