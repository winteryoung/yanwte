package integrationTests.mixedTest;

interface TestExtensionPoint {
    fun foo(testData: TestData): String?
}