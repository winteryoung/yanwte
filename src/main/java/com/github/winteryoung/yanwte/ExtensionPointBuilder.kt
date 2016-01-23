package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.combinators.ChainCombinator
import com.github.winteryoung.yanwte.internals.combinators.EmptyCombinator
import com.github.winteryoung.yanwte.internals.combinators.LeafExtensionCombinator
import com.github.winteryoung.yanwte.internals.combinators.MapReduceCombinator
import java.lang.reflect.Method

/**
 * An extension point builder can build an instance of an extension point.
 * The only thing you must provide is the extension tree.
 *
 * *Note*. The [build] process is expensive, because it uses reflection.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
open class ExtensionPointBuilder(
        /**
         * The SAM interface that represents this extension point.
         */
        val extensionPointInterface: Class<*>
) {
    val extensionPointName: String = extensionPointInterface.name

    /**
     * The extension tree of the extension point.
     */
    var tree: Combinator = EmptyCombinator(extensionPointName)

    /**
     * Build the extension point instance.
     */
    internal fun build(): ExtensionPoint {
        val method = parseMethod(extensionPointInterface)
        return ExtensionPoint(extensionPointName, extensionPointInterface, method).apply {
            this.combinator = tree
        }
    }

    /**
     * Build and extension point instance and register it in [YanwteContainer].
     */
    fun buildAndRegister() {
        val extPoint = build()
        YanwteContainer.registerExtensionPoint(extPoint)
    }

    private fun parseMethod(samInterface: Class<*>): Method {
        val size = samInterface.declaredMethods.size
        if (size != 1) {
            throw YanwteException("A SAM interface is required for extension point $extensionPointName")
        }

        return samInterface.declaredMethods[0]
    }

    /**
     * Returns the extension combinator of the given class. [extensionClass] must have a
     * parameterless constructor.
     */
    fun extOfClass(extensionClass: Class<*>): Combinator {
        val extensionName = extensionClass.name
        val plugin = YanwtePlugin.getPluginByExtensionName(extensionName)
        val extPojo = plugin.getExtensionByName(extensionName)
                ?: throw YanwteException("Cannot find extension POJO with name $extensionName")
        return LeafExtensionCombinator(extensionPointName, YanwteExtension.fromPojo(extPojo))
    }

    /**
     * In case anyone wants to write an empty provide, this combinator can help.
     * It does nothing but returns null on any input.
     */
    fun empty(): Combinator = EmptyCombinator(extensionPointName)

    /**
     * A chain of responsibility combinator passes a given input to each sub nodes sequentially,
     * and stops and returns the value returned by the node that returns a non-null value.
     * This node has the semantics of short-circuit.
     */
    fun chain(vararg nodes: Combinator): Combinator =
            ChainCombinator(extensionPointName, nodes.toList())

    /**
     * A map reduce combinator passes the input to each sub nodes sequentially,
     * and reduce (by calling [reducer]) those outputs returned by the sub nodes
     * to a single output, and return that output.
     *
     * The param [T] refers to the return type of the SAM extension point interface.
     */
    fun <T> mapReduce(
            /**
             * Sub nodes
             */
            nodes: List<Combinator>,
            /**
             * The function that reduces the results returned by [nodes] to a single result.
             *
             * *Note*, although each [ExtensionPointOutput] in the parameter list cannot be null,
             * but [ExtensionPointOutput.returnValue] can be null
             */
            reducer: (List<T>) -> T
    ): Combinator = MapReduceCombinator(extensionPointName, nodes, reducer)
}
