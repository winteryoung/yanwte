package integrationTests.mixedTest

import com.github.winteryoung.yanwte.DataExtensionPoint
import com.github.winteryoung.yanwte.YanwteContainer
import com.github.winteryoung.yanwte.YanwteOptions
import org.junit.*

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
