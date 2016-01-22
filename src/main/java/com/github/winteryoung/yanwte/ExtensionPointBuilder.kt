package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.YanwteExtension
import com.github.winteryoung.yanwte.internals.trees.EmptyExtensionTree
import com.github.winteryoung.yanwte.internals.trees.LeafExtensionTree
import com.github.winteryoung.yanwte.internals.trees.MutexExtensionTree
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
class ExtensionPointBuilder(
        /**
         * The SAM interface that represents this extension point.
         */
        val extensionPointInterface: Class<*>
) {
    val extensionPointName = extensionPointInterface.name

    /**
     * The extension tree of the extension point.
     */
    var tree: ExtensionTree = EmptyExtensionTree(extensionPointName)

    /**
     * Build the extension point instance.
     */
    internal fun build(): ExtensionPoint {
        val method = parseMethod(extensionPointInterface)
        return ExtensionPoint(extensionPointName, extensionPointInterface, method).apply {
            this.extensionTree = tree
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
     * Returns the extension tree node of the given class. [extensionClass] must have a
     * parameterless constructor.
     */
    fun extOfClass(extensionClass: Class<*>): ExtensionTree {
        val extensionName = extensionClass.name
        val plugin = YanwtePlugin.getPluginByExtensionName(extensionName)
        val extPojo = plugin.getExtensionByName(extensionName)
                ?: throw YanwteException("Cannot find extension POJO with name $extensionName")
        return LeafExtensionTree(extensionPointName, YanwteExtension.fromPojo(extPojo))
    }

    /**
     * In case anyone wants to write an empty provide, this node can help.
     * It does nothing but returns null on any input.
     */
    fun empty(): ExtensionTree = EmptyExtensionTree(extensionPointName)

    /**
     * A mutex semantic node pass a given input to each sub nodes sequentially,
     * and stops and returns the value returned by the node that returns a non-null value.
     * This node has the semantics of short-circuit, just like the responsibility chain pattern.
     */
    fun mutex(vararg nodes: ExtensionTree): ExtensionTree =
            MutexExtensionTree(extensionPointName, nodes.toList())
}
