package com.github.winteryoung.yanwte.internals

import com.github.winteryoung.yanwte.ExtensionPointBuilder
import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.YanwteContainer
import com.github.winteryoung.yanwte.YanwteException
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Winter Young
 * @since 2016/1/21
 */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class ExtensionPointTest {
    @Test
    fun testExtensionPointExecution() {
        ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = extOfClass(TestExtension::class.java)
        }.buildAndRegister()
        val extPoint = YanwteContainer.getExtensionPointByName(TestExtensionPoint::class.java.name)!!
        val (output) = extPoint(ExtensionPointInput(listOf(3, "4")))

        Assert.assertEquals(7, output)
    }

    @Test
    fun testExecutionOfPojoExtensionPoint() {
        ExtensionPointBuilder(TestExtensionPoint::class.java).apply {
            tree = extOfClass(TestExtension::class.java)
        }.buildAndRegister()
        val extPoint = YanwteContainer.getExtensionPointByClass(TestExtensionPoint::class.java)!!
        val output = extPoint.foo(3 as Integer, "4")

        Assert.assertEquals(7, output)
    }

    @Test
    fun testExecutionOfPojoExtensionPointReturningVoid() {
        ExtensionPointBuilder(TestExtensionPointReturningVoid::class.java).apply {
            tree = extOfClass(TestExtensionReturningVoid::class.java)
        }.buildAndRegister()
        val extPoint = YanwteContainer.getExtensionPointByClass(TestExtensionPointReturningVoid::class.java)!!
        val param = AtomicInteger(3)
        extPoint.foo(param)

        Assert.assertEquals(5, param.get())
    }

    @Test
    fun testExecutionOfPojoExtensionPointWithPrimitiveTypes() {
        ExtensionPointBuilder(TestExtensionPointWithPrimitiveTypes::class.java).apply {
            tree = empty()
        }.buildAndRegister()

        try {
            YanwteContainer.getExtensionPointByClass(TestExtensionPointWithPrimitiveTypes::class.java)!!
            Assert.fail()
        } catch (e: YanwteException) {
            Assert.assertEquals(
                    "Primitives are not supported: ${TestExtensionPointWithPrimitiveTypes::class.java.name}",
                    e.message
            )
        }
    }

    interface TestExtensionPoint {
        fun foo(a: Integer, b: String): Integer
    }

    class TestExtension : TestExtensionPoint {
        override fun foo(a: Integer, b: String): Integer {
            return (a.toInt() + b.toInt()) as Integer
        }
    }

    interface TestExtensionPointWithPrimitiveTypes {
        fun foo(a: Int): Int
    }

    interface TestExtensionPointReturningVoid {
        fun foo(a: AtomicInteger)
    }

    class TestExtensionReturningVoid : TestExtensionPointReturningVoid {
        override fun foo(a: AtomicInteger) {
            a.set(5)
        }
    }
}