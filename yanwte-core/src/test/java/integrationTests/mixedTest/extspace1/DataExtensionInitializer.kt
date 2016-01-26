package integrationTests.mixedTest.extspace1

import com.github.winteryoung.yanwte.DataExtensionPoint
import com.github.winteryoung.yanwte.YanwteDataExtensionInitializer
import integrationTests.mixedTest.TestData
import integrationTests.mixedTest.TestDataExt

class DataExtensionInitializer : YanwteDataExtensionInitializer() {
    override fun initialize(dataExtensionPoint: DataExtensionPoint): Any? {
        return when (dataExtensionPoint) {
            is TestData -> {
                TestDataExt(dataExtensionPoint.i)
            }
            else -> {
                null
            }
        }
    }
}