package integrationTests.mixedTest.extspace1

import integrationTests.mixedTest.TestData
import integrationTests.mixedTest.TestDataExt
import integrationTests.mixedTest.TestExtensionPoint

class TestExtension1 : TestExtensionPoint {
    override fun foo(testData: TestData): String? {
        val (i) = testData.getDataExtension<TestDataExt>()!!
        return (i + 1).toString()
    }
}