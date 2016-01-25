package com.github.winteryoung.yanwte

/**
 * A class implementing data extension point means it is able to extend its data.
 * Compared to `ExtensionPoint`, you can think of that as a kind of behavioral
 * extension point. This is focused on data.
 *
 * @author Winter Young
 * @since 2016/1/25
 */
interface DataExtensionPoint {
    /**
     * Returns the data extension bound to the extension space calculated
     * from the given extension.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getDataExtension(extension: Any): T? {
        return YanwteContainer.getExtensionByPojo(extension)!!.let {
            it.extensionSpaceName.let { extSpaceName ->
                YanwteContainer.getDataExtension(this, extSpaceName).let { dataExt ->
                    dataExt as T ?: initDataExt(extSpaceName, extension, this)?.let { newDataExt ->
                        YanwteContainer.registerDataExtension(this, extSpaceName, newDataExt)
                        newDataExt as T
                    }
                }
            }
        }
    }

    private fun initDataExt(extSpaceName: String, extension: Any, dataExtensionPoint: DataExtensionPoint): Any? {
        return getDataExtInitializer(extSpaceName, extension)?.let { dataExtInitializer ->
            dataExtInitializer.initialize(dataExtensionPoint)
        }
    }

    private fun getDataExtInitializer(extSpaceName: String, extension: Any): YanwteDataExtensionInitializer? {
        YanwteContainer.getDataExtInitializer(extSpaceName)?.let {
            return it
        }

        return extension.javaClass.classLoader.let {
            it.loadClass("$extSpaceName.DataExtensionInitializer").let {
                @Suppress("UNCHECKED_CAST")
                val initClass = it as Class<YanwteDataExtensionInitializer>
                initClass.newInstance().let { initializer ->
                    YanwteContainer.registerDataExtInitializer(extSpaceName, initializer)
                }
                YanwteContainer.getDataExtInitializer(extSpaceName)
            }
        }
    }
}
