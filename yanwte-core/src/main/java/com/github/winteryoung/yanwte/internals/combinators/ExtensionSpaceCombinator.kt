package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.YanwteExtensionSpace

/**
 * A combinator that finds the corresponding extension by its extension space.
 * There's only one extension allowed to exists in one extension space corresponding
 * to a given extension point.
 *
 * @author Winter Young
 * @since 2016/12/3
 */
internal class ExtensionSpaceCombinator(
        extensionPointInterface: Class<*>,
        extensionSpace: Class<out YanwteExtensionSpace>,
        failOnExtensionNotFound: Boolean
) : ExtensionAwareCombinatorProxy(
        ExtensionSpaceNameCombinator(
                extensionPointInterface, extensionSpace.`package`.name, failOnExtensionNotFound
        ),
        extensionPointInterface.name,
        "extSpace"
)