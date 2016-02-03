package integrationTests.mixedTest

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.DataExtensionPoint
import com.github.winteryoung.yanwte.ExtensionPointProvider
import com.github.winteryoung.yanwte.YanwteContainer
import integrationTests.mixedTest.extspace1.TestExtension1
import integrationTests.mixedTest.extspace2.TestExtension2
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

class TestExtensionPointProvider : ExtensionPointProvider() {
    override fun tree(): Combinator {
        return chain(
                extOfClass(TestExtension1::class.java),
                extOfClass(TestExtension2::class.java)
        )
    }
}

