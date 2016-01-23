package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.ExtensionPointBuilder
import com.github.winteryoung.yanwte.YanwteContainer
import org.junit.Assert
import org.junit.Test

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
/**
 * @author Winter Young
 * @since 2016/1/23
 */
class MapReduceCombinatorTest {
    @Test
    fun test() {
        ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = mapReduce<Integer>(listOf(
                    extOfClass(Extension1::class.java),
                    extOfClass(Extension2::class.java)
            )) { outputs ->
                outputs.reduce { a, b ->
                    val c = a.toInt() + b.toInt()
                    c as Integer
                }
            }
        }.buildAndRegister()

        val output = YanwteContainer.getExtensionPointByClass(TestExtensionPoint::class.java)!!.foo()

        Assert.assertEquals(5, output)
    }

    interface TestExtensionPoint {
        fun foo(): Integer
    }

    class Extension1 : TestExtensionPoint {
        override fun foo(): Integer {
            return 2 as Integer
        }
    }

    class Extension2 : TestExtensionPoint {
        override fun foo(): Integer {
            return 3 as Integer
        }
    }
}