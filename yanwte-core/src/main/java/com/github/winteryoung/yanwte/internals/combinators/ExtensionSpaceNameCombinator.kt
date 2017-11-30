package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.YanwteException
import com.google.common.reflect.ClassPath

/**
 * A combinator that finds the corresponding extension by its extension space name.
 *
 * @author Winter Young
 * @since 2016/12/3
 * @see [ExtensionSpaceCombinator]
 */
internal class ExtensionSpaceNameCombinator(
        extensionPointInterface: Class<*>,
        extensionSpaceName: String,
        failOnExtensionNotFound: Boolean
) : ExtensionAwareCombinatorProxy(
        buildCombinatorFromSpaceAndExtPoint(extensionSpaceName, extensionPointInterface, failOnExtensionNotFound),
        extensionPointInterface.name,
        "extSpaceName"
)

private fun buildCombinatorFromSpaceAndExtPoint(
        extensionSpaceName: String,
        extensionPointInterface: Class<*>,
        failOnExtensionNotFound: Boolean
): ExtensionAwareCombinator {
    val extClassCandidates = scanClasses(extensionSpaceName, extensionPointInterface)

    if (extClassCandidates.size > 1) {
        throw YanwteException("At most one extension for ${extensionPointInterface.name}" +
                " is allowed for extension space: $extensionSpaceName")
    }

    if (extClassCandidates.isEmpty()) {
        if (failOnExtensionNotFound) {
            throw YanwteException("At least one extension for ${extensionPointInterface.name}" +
                    " is expected for extension space: $extensionSpaceName")
        } else {
            EmptyCombinator(extensionPointInterface.name)
        }
    }

    val extClass = extClassCandidates.single()
    return ExtensionNameCombinator(extClass.name, extensionPointInterface, failOnExtensionNotFound)
}

private fun scanClasses(extensionSpaceName: String, extensionPointInterface: Class<*>): List<Class<*>> {
    ClassPath.from(Thread.currentThread().contextClassLoader)
            .getTopLevelClassesRecursive(extensionSpaceName)
            .map {
                it.load()
            }
            .filter {
                extensionPointInterface.isAssignableFrom(it)
            }
            .let {
                return it
            }
}
