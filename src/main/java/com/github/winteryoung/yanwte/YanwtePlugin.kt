package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.DefaultYanwtePlugin
import com.github.winteryoung.yanwte.internals.utils.PackageNamedKeyMap

/**
 * A Yanwte plugin maintains the entrance to query extensions.
 * Because the lifecycle of the extensions may be maintained by another container,
 * like Spring. So we don't intervene the object creation process directly.
 *
 * The objects returned by the methods in this interface are all should be POJOs.
 * Converting the POJOs to Yanwte internal data structures are the job of Yanwte
 * internals.
 *
 * @author Winter Young
 * @since 2016/1/21
 */
interface YanwtePlugin {
    /**
     * Get extension by name.
     */
    fun getExtensionByName(extensionName: String): Any?

    companion object {
        private val packageNameToPlugin = PackageNamedKeyMap<YanwtePlugin>()
        private val defaultPlugin = DefaultYanwtePlugin()

        internal fun clearPluginRegistry() {
            packageNameToPlugin.clear()
        }

        /**
         * Register plugin by package name. Any extension that have the
         */
        fun registerPlugin(plugin: YanwtePlugin, packageName: String) {
            val pn = packageName.trim().trim('.')
            if (packageNameToPlugin.containsKey(pn)) {
                throw YanwteException("Package $pn has already been registered by plugin $plugin")
            }
            packageNameToPlugin[pn] = plugin
        }

        /**
         * Returns the plugin matching the given extension name. The matching process is like this:
         *
         * * Find the package name of the extension
         * * If the package name is registered by a plugin, returns that plugin
         * * If not, recursively find the plugin that matching any ancestor of the package name
         *
         * If not found, finally that will resort to the default plugin.
         */
        fun getPluginByExtensionName(extensionName: String): YanwtePlugin {
            val packageName = extensionName.substringBeforeLast(".")
            return packageNameToPlugin[packageName] ?: defaultPlugin
        }
    }
}