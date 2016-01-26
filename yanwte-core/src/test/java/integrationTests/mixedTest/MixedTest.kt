package integrationTests.mixedTest

import com.github.winteryoung.yanwte.DataExtensionPoint
import com.github.winteryoung.yanwte.ExtensionPointBuilder
import com.github.winteryoung.yanwte.YanwteContainer
import com.github.winteryoung.yanwte.YanwteDataExtensionInitializer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/25
 */
class MixedTest {
    @Before
    fun before() {
        YanwteContainer.clear()
    }

    @After
    fun after() {
        YanwteContainer.clear()
    }

    @Test
    fun test() {
        ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = chain(
                    extOfClass(TestExtension1::class.java),
                    extOfClass(TestExtension2::class.java)
            )
        }.buildAndRegister()

        val testExtensionPoint = YanwteContainer.getExtensionPointByClass(TestExtensionPoint::class.java)!!

        testExtensionPoint.foo(TestData(3)).let {
            Assert.assertEquals("2", it)
        }

        testExtensionPoint.foo(TestData(4)).let {
            Assert.assertEquals("5", it)
        }
    }
}

class TestData(
        val i: Int
) : DataExtensionPoint

data class TestDataExt(
        val i: Int
)

interface TestExtensionPoint {
    fun foo(testData: TestData): String?
}

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

class TestExtension2 : TestExtensionPoint {
    override fun foo(testData: TestData): String? {
        val testDataExt = testData.getDataExtension<TestDataExt>()!!
        return testDataExt.i.let { i ->
            if (i % 2 != 0) i - 1 else null
        }?.let {
            it.toString()
        }
    }
}

class DataExtensionInitializer : YanwteDataExtensionInitializer {
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