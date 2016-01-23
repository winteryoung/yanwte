package com.github.winteryoung.yanwte

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/19
 */
class ExtensionPointBuilderTest {
    @Before
    fun before() {
        YanwteContainer.clear()
    }

    @After
    fun after() {
        YanwteContainer.clear()
    }

    @Test
    fun testEmptyApiAcceptance() {
        val extensionPoint = ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = empty()
        }.build()

        val (output) = extensionPoint(ExtensionPointInput(listOf(3)))

        Assert.assertNull(output)
    }

    @Test
    fun testExtApiAcceptance() {
        val extensionPoint = ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = extOfClass(TestExtension::class.java)
        }.build()

        Assert.assertNotNull(extensionPoint)
    }

    @Test
    fun testChainApiAcceptance() {
        val extensionPoint = ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = chain(
                    extOfClass(TestExtension::class.java)
            )
        }.build()

        Assert.assertNotNull(extensionPoint)
    }

    @Test
    fun testMapReduceApiAcceptance() {
        val ep = ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = mapReduce<Int>(listOf(
                    extOfClass(TestExtension::class.java)
            )) { outputs ->
                outputs.fold(0) { acc, next ->
                    acc + next
                }
            }
        }

        Assert.assertNotNull(ep)
    }

    interface TestExtensionPoint {
        fun test(a: Int): Int
    }

    class TestExtension : TestExtensionPoint {
        override fun test(a: Int): Int {
            return a
        }
    }
}
