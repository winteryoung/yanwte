package integrationTests.mixedTest.extspace2

import integrationTests.mixedTest.TestData
import integrationTests.mixedTest.TestDataExt
import integrationTests.mixedTest.TestExtensionPoint

class TestExtension2 : TestExtensionPoint {
    override fun foo(testData: TestData): String? {
        val testDataExt = testData.getDataExtension<TestDataExt>("integrationTests.mixedTest.extspace1")!!
        return testDataExt.i.let { i ->
            if (i % 2 != 0) i - 1 else null
        }?.let {
            it.toString()
        }
    }
}