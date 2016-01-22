package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.trees.LeafExtensionTree
import java.util.*

/**
 * Extensions of a given extension point are organized as a tree, represented by this class.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
abstract class ExtensionTree(
        /**
         * The name of the corresponding extension point.
         */
        val extensionPointName: String,
        /**
         * Sub nodes.
         */
        val nodes: List<ExtensionTree>,
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
    operator final fun invoke(input: ExtensionPointInput): ExtensionPointOutput {
        return invokeImpl(input)
    }

    /**
     * Client overrides this method to provide the tree node logic.
     */
    protected abstract fun invokeImpl(input: ExtensionPointInput): ExtensionPointOutput

    internal fun collectDependentExtensions(): List<YanwteExtension> {
        fun recur(accumulator: MutableList<YanwteExtension>, tree: ExtensionTree) {
            if (tree is LeafExtensionTree) {
                accumulator.add(tree.extension)
            } else {
                for (node in nodes) {
                    recur(accumulator, node)
                }
            }
        }

        val result = ArrayList<YanwteExtension>()
        recur(result, this)

        return result
    }
}