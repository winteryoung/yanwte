package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput

/**
 * A chain tree node pass a given input to each sub nodes sequentially,
 * and stops and returns the value returned by the node that returns a non-null value.
 * This node has the semantics of short-circuit, just like the responsibility chain pattern.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
internal class ChainCombinator(
        extensionPointName: String,
        nodes: List<Combinator>
) : Combinator(extensionPointName, nodes, "mutex") {
    override fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput {
        for (node in nodes) {
            val (output) = node(input)
            if (output != null) {
                return ExtensionPointOutput(output)
            }
        }

        return ExtensionPointOutput.empty
    }
}