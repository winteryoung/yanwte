package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
/**
 * @author Winter Young
 * @since 2016/1/19
 */
class YanwteExtensionTest {
    @Before
    fun before() {
        YanwteContainer.clear()
    }

    @After
    fun after() {
        YanwteContainer.clear()
    }

    @Test
    fun testSimpleExtensionExecution() {
        val ext = YanwteExtension("testExt", ExtensionExecution { input ->
            val (args) = input
            val (arg) = args
            ExtensionPointOutput(arg as Int * 2)
        })
        val (value) = ext(ExtensionPointInput(listOf(3)))
        Assert.assertEquals(6, value as Int)
    }

    @Test
    fun testPojoExtensionExecution() {
        ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = extOfClass(TestExtension::class.java)
        }.buildAndRegister()

        val ext = YanwteContainer.getExtensionByName(TestExtension::class.java.name)!!
        val (output) = ext(ExtensionPointInput(listOf(3, 4)))
        Assert.assertEquals(7, output)
    }

    @Test
    fun testFromPojoCaseNoSamInterfaceImplementedByExtension() {
        try {
            YanwteExtension.fromPojo(BadExtension())
            Assert.fail()
        } catch (e: YanwteException) {
            val expectedMsg = "Cannot find extension point for ${BadExtension::class.java.name}," +
                    " no SAM interface found"
            Assert.assertEquals(expectedMsg, e.message)
        }
    }

    class BadExtension

    interface TestExtensionPoint {
        fun foo(a: Integer, b: Integer): Integer
    }

    class TestExtension : TestExtensionPoint {
        override fun foo(a: Integer, b: Integer): Integer {
            val c = a.toInt() + b.toInt()
            return c as Integer
        }
    }
}