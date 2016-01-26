package integrationTests.mixedTest.extspace1

import integrationTests.mixedTest.TestData
import integrationTests.mixedTest.TestDataExt
import integrationTests.mixedTest.TestExtensionPoint

class TestExtension1 : TestExtensionPoint {
    override fun foo(testData: TestData): String? {
        val testDataExt = testData.getDataExtension<TestDataExt>()!!
        return testDataExt.i.let { i ->
            if (i % 2 == 0) i + 1 else null
        }?.let {
            it.toString()
        }
    }
}