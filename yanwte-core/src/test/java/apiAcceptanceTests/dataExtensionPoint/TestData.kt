package apiAcceptanceTests.dataExtensionPoint;

import com.github.winteryoung.yanwte.DataExtensionPoint
import integrationTests.mixedTest.TestData

class TestData(
        val i: Int
) : DataExtensionPoint

data class TestDataExt(
        val i: Int
)

interface TestExtensionPoint {
    fun foo(testData: TestData): String?
}