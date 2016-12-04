package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.combinators.ExtensionAwareCombinator
import com.github.winteryoung.yanwte.internals.combinators.ExtensionCombinator
import java.util.*

/**
 * Extensions of a given extension point are organized as a tree, represented by this class.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
abstract class Combinator(
        /**
         * The name of the corresponding extension point.
         */
        val extensionPointName: String,
        /**
         * Sub nodes.
         */
        val nodes: List<Combinator>,
        /**
         * Extension tree node name.
         */
        val name: String
) {
    init {
        for (node in nodes) {
            if (node.extensionPointName != extensionPointName) {
                throw IllegalArgumentException("Expects extension point name $extensionPointName" +
                        " for parent node $name," +
                        " but got ${node.extensionPointName} for sub node ${node.name}")
            }
        }
    }

    /**
     * Invoke this tree.
     */
    operator fun invoke(input: ExtensionPointInput): ExtensionPointOutput {
        return invokeImpl(input)
    }

    /**
     * Client overrides this method to provide the tree node logic.
     */
    protected abstract fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput

    internal fun collectDependentExtensions(): List<YanwteExtension> {
        fun recur(accumulator: MutableList<YanwteExtension>, tree: Combinator) {
            if (tree is ExtensionAwareCombinator) {
                val extension = tree.extension
                if (extension != null) {
                    accumulator.add(extension)
                }
            } else {
                for (node in tree.nodes) {
                    recur(accumulator, node)
                }
            }
        }

        val result = ArrayList<YanwteExtension>()
        recur(result, this)

        return result
    }

    override fun toString(): String {
        return "Combinator(extensionPointName='$extensionPointName', name='$name')"
    }
}