package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput

/**
 * See doc of [ExtensionPointProvider.mapReduce].
 *
 * @author Winter Young
 * @since 2016/1/23
 */
internal class MapReduceCombinator<T>(
        extensionPointName: String,
        nodes: List<Combinator>,
        private val reducer: (List<T>) -> T
) : Combinator(extensionPointName, nodes, "mapReduce") {
    override fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput {
        return nodes.map {
            @Suppress("UNCHECKED_CAST")
            (it(input).returnValue as T)
        }.let { outputs ->
            ExtensionPointOutput(reducer(outputs))
        }
    }
}