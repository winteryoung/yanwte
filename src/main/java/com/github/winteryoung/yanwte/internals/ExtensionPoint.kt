package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.internals.combinators.EmptyCombinator
import java.lang.reflect.Method

/**
 * Extension point represents an abstract function, a SAM interface.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
internal class ExtensionPoint(
        /**
         * The name of this extension point.
         */
        val name: String,
        /**
         * The SAM interface representing this extension point.
         */
        val samInterface: Class<*>,
        /**
         * The method of this extension point.
         */
        val method: Method
) {
    private var _combinator: Combinator = EmptyCombinator(name)
    /**
     * Extension tree.
     */
    var combinator: Combinator
        set(value) {
            if (name != value.extensionPointName) {
                throw IllegalArgumentException("Different names, expects $name," +
                        " but got ${combinator.extensionPointName}")
            }
            this._combinator = value
        }
        get() = _combinator

    /**
     * Invokes this extension point.
     */
    operator fun invoke(input: ExtensionPointInput): ExtensionPointOutput {
        return combinator(input)
    }
}