package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.*
import com.github.winteryoung.yanwte.internals.bytecode.generateExtensionExecutionDelegate
import org.slf4j.LoggerFactory

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
    /**
     * The extension space name.
     */
    val extensionSpaceName: String by lazyOf(name.substringBeforeLast(".", ""))

    /**
     * The extension space instance.
     */
    val extensionSpace: YanwteExtensionSpace by lazy {
        YanwteContainer.getExtensionSpaceByName(extensionSpaceName)!!
    }

    /**
     * Invokes this extension.
     */
    operator fun invoke(input: ExtensionPointInput): ExtensionPointOutput {
        try {
            YanwteRuntime.currentRunningExtension = this

            if (input.args.isNotEmpty()) {
                input.args[0]?.let { domainObj ->
                    YanwteContainer.getBizRecognizerResult(domainObj, extensionSpaceName)?.let {
                        if (!it) {
                            return ExtensionPointOutput.empty
                        }
                    } ?: run {
                        runBizRecognizer(domainObj, extensionSpaceName).let {
                            YanwteContainer.cacheBizRecognizerResult(domainObj, extensionSpaceName, it)
                            if (!it) {
                                return ExtensionPointOutput.empty
                            }
                        }
                    }
                }
            }

            return action(input)
        } finally {
            YanwteRuntime.currentRunningExtension = null
        }
    }

    private fun runBizRecognizer(domainObj: Any, extensionSpaceName: String): Boolean {
        return YanwteContainer.getBizRecognizer(extensionSpaceName)?.recognizes(domainObj) ?: true
    }

    companion object {
        private val log = LoggerFactory.getLogger(YanwteExtension::class.java)

        /**
         * Constructs [YanwteExtension] from a POJO instance.
         */
        fun fromPojo(extensionPoint: Class<*>, extension: Any): YanwteExtension {
            val extClass = extension.javaClass
            val extPointName = extensionPoint.name
            val extName = extClass.name

            if (YanwteOptions.logExtensionsBuild && log.isWarnEnabled) {
                log.warn("Build extension from POJO class $extClass, extName: $extName, extPointName: $extPointName")
            }

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
    }
}