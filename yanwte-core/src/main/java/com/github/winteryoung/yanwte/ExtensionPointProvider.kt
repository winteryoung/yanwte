package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.combinators.*
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

/**
 * An extension point provider can build an instance of an extension point.
 * The only thing you must provide is the extension tree.
 *
 * @author Winter Young
 */
abstract class ExtensionPointProvider {
    /**
     * Build the extension point instance.
     */
    internal fun getExtensionPoint(): ExtensionPoint {
        val method = parseMethod(extensionPointInterface)
        return ExtensionPoint(extensionPointName, extensionPointInterface, method).apply {
            this.combinator = tree()
        }
    }

    private fun parseMethod(samInterface: Class<*>): Method {
        val size = samInterface.declaredMethods.size
        if (size != 1) {
            throw YanwteException("A SAM interface is required for extension point $extensionPointName")
        }

        return samInterface.declaredMethods[0]
    }

    /**
     * The extension point name.
     */
    private val extensionPointName: String by lazy {
        this.javaClass.name.let {
            it.substring(0, it.lastIndexOf("Provider"))
        }
    }

    val extensionPointInterface: Class<*> by lazy {
        this.javaClass.classLoader.let {
            try {
                it.loadClass(extensionPointName)
            } catch (e: ClassNotFoundException) {
                throw YanwteException("Cannot find extension point $extensionPointName", e)
            }
        }
    }

    /**
     * Client overrides this method to provide the extension tree of the extension point.
     */
    protected abstract fun tree(): Combinator

    /**
     * Throws an exception if no extension can be found for the given name or class.
     * The default is true, so it can fail-fast. Client can override this property.
     *
     * This method can affect
     * * [extOfClass]
     * * [extOfClassName]
     */
    open protected val isFailOnExtensionNotFound: Boolean = true

    /**
     * Returns the extension combinator of the given class. [extensionClass] must have a
     * parameterless constructor.
     */
    fun extOfClass(extensionClass: Class<*>): Combinator {
        if (YanwteOptions.logExtensionsBuild && log.isWarnEnabled) {
            log.warn("extOfClass, extension class: $extensionClass")
        }
        val extensionName = extensionClass.name
        return extOfClassName(extensionName)
    }

    /**
     * Returns the extension combinator of the given class name. The extension class must have
     * a parameterless constructor.
     */
    fun extOfClassName(extensionClassName: String): Combinator {
        return ExtensionNameCombinator(extensionClassName, extensionPointInterface, isFailOnExtensionNotFound)
    }

    /**
     * Returns the extension combinator of the given extension space. The extension class must
     * have a parameterless constructor. The given extension space must have at most one
     * extension that implements this extension point. Otherwise, an exception will be thrown.
     */
    fun extOfExtSpace(extensionSpace: Class<out YanwteExtensionSpace>): Combinator =
            ExtensionSpaceCombinator(extensionPointInterface, extensionSpace, isFailOnExtensionNotFound)

    /**
     * Returns the extension combinator of the given extension space. The extension class must
     * have a parameterless constructor. The given extension space must have at most one
     * extension that implements this extension point. Otherwise, an exception will be thrown.
     */
    fun extOfExtSpaceName(extensionSpaceName: String): Combinator =
            ExtensionSpaceNameCombinator(extensionPointInterface, extensionSpaceName, isFailOnExtensionNotFound)

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

    companion object {
        private val log = LoggerFactory.getLogger(ExtensionPointProvider::class.java)
    }
}
