package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.*
import com.github.winteryoung.yanwte.internals.YanwteExtension

/**
 * A combinator that finds the corresponding extension of a given name.
 *
 * @author Winter Young
 * @since 2016/12/3
 * @param failOnExtensionNotFound [com.github.winteryoung.yanwte.ExtensionPointProvider.isFailOnExtensionNotFound]
 */
internal class ExtensionNameCombinator(
        extensionName: String,
        extensionPointInterface: Class<*>,
        failOnExtensionNotFound: Boolean
) : ExtensionAwareCombinatorProxy(
        findExtOfName(extensionName, extensionPointInterface, failOnExtensionNotFound),
        extensionPointInterface.name,
        "extName"
)

private fun findExtOfName(
        extensionName: String, extensionPointInterface: Class<*>, failOnExtensionNotFound: Boolean
): ExtensionAwareCombinator {
    YanwtePlugin.getPluginByExtensionName(extensionName).let { plugin ->
        plugin.getExtensionByName(extensionName).let { extPojo ->
            if (extPojo == null) {
                if (failOnExtensionNotFound) {
                    throw YanwteException("Cannot find extension POJO with name: $extensionName")
                } else {
                    return EmptyCombinator(extensionPointInterface.name)
                }
            } else {
                val extension = YanwteExtension.fromPojo(extensionPointInterface, extPojo)
                return ExtensionCombinator(extensionPointInterface.name, extension)
            }
        }
    }
}
