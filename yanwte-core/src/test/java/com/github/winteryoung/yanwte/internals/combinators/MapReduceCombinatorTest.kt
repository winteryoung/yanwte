package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointProvider
import com.github.winteryoung.yanwte.YanwteContainer
import org.junit.Assert
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/23
| */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class MapReduceCombinatorTest {
    @Test
    fun test() {
        val output = YanwteContainer.getExtensionPointByClass(TestExtensionPoint::class.java)!!.foo()
        Assert.assertEquals(5, output)
    }

    interface TestExtensionPoint {
        fun foo(): Integer?
    }

    class TestExtensionPointProvider : ExtensionPointProvider() {
        override fun tree(): Combinator {
            return mapReduce<Integer?>(listOf(
                    extOfClass(Extension1::class.java),
                    extOfClass(Extension2::class.java)
            )) { outputs ->
                outputs.filter { it != null }.reduce { a, b ->
                    val c = a!!.toInt() + b!!.toInt()
                    c as Integer
                }
            }
        }
    }

    class Extension1 : TestExtensionPoint {
        override fun foo(): Integer? {
            return 2 as Integer
        }
    }

    class Extension2 : TestExtensionPoint {
        override fun foo(): Integer? {
            return 3 as Integer
        }
    }

    class Extension3 : TestExtensionPoint {
        override fun foo(): Integer? {
            return null
        }
    }
}