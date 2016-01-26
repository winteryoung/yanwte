package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.YanwteContainer
import com.github.winteryoung.yanwte.YanwteException
import com.github.winteryoung.yanwte.internals.bytecode.generateExtensionExecutionDelegate

/**
 * A Yanwte extension is an implementation to an extension point.
 *
 * @author Winter Young
 * @since 2016/1/17
 */
internal class YanwteExtension(
        /**
         * The name of the extension.
         */
        val name: String,
        /**
         * The POJO extension object that corresponds to this Yanwte extension.
         * For testing purpose, this parameter can be null.
         */
        val pojoExtension: Any?,
        /**
         * The actual action of the extension.
         */
        val action: (ExtensionPointInput) -> ExtensionPointOutput
) {
    val extensionSpaceName = name.substringBeforeLast(".", "")

    /**
     * Invokes this extension.
     */
    operator fun invoke(input: ExtensionPointInput): ExtensionPointOutput {
        try {
            YanwteRuntime.currentRunningExtension = this
            return action(input)
        } finally {
            YanwteRuntime.currentRunningExtension = null
        }
    }

    companion object {
        /**
         * Constructs [YanwteExtension] from a POJO instance.
         */
        fun fromPojo(extension: Any): YanwteExtension {
            val extClass = extension.javaClass
            val (extPointName, extName) = parseExtensionClass(extClass)
            return YanwteExtension(extName, extension) { input ->
                val extPoint = YanwteContainer.getExtensionPointByName(extPointName)
                if (extPoint == null) {
                    val exMsg = "Cannot find extension point $extPointName for extension $extName"
                    throw YanwteException(exMsg)
                }

                val proxy = generateExtensionExecutionDelegate(extPoint, extension)
                proxy.execute(input)
            }
        }

        private fun isSamInterface(cls: Class<*>) = cls.isInterface && cls.declaredMethods.size == 1

        private fun parseExtPointClass(cls: Class<*>?): Class<*>? {
            if (cls == null) {
                return null
            }
            if (isSamInterface(cls)) {
                return cls
            }
            parseExtPointClass(cls.superclass)?.let { cls ->
                return cls
            }
            for (interfaceClass in cls.interfaces) {
                parseExtPointClass(interfaceClass)?.let { cls ->
                    return cls
                }
            }
            return null
        }

        private fun parseExtensionClass(extClass: Class<*>): Pair<String, String> {
            val extPointName = parseExtPointClass(extClass)?.name
                    ?: throw YanwteException("Cannot find extension point for ${extClass.name}, no SAM interface found")

            return extPointName to extClass.name
        }
    }
}