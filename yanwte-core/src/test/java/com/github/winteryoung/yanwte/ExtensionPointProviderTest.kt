package com.github.winteryoung.yanwte

import org.junit.Assert
import org.junit.Test

/**
 * @author Winter Young
 */
class ExtensionPointProviderTest {
    @Test
    fun testExtOfClassFailingOnExtensionNotFound() {
        try {
            TestExtensionPointProvider().getExtensionPoint()
            Assert.fail()
        } catch (e: YanwteException) {
            Assert.assertEquals("Cannot find extension POJO with name nonExistentExt", e.message)
        }
    }

    @Test
    fun testExtOfClassReturningEmptyCombinatorOnExtensionNotFound() {
        TestExtensionPointProvider2().getExtensionPoint().let { extPoint ->
            Assert.assertNotNull(extPoint)
        }
    }

    @Test
    fun testProviderWhichHasNoExtensionPointInterface() {
        try {
            ProviderWhichHasNoExtensionPointInterface().getExtensionPoint()
            Assert.fail()
        } catch (e: YanwteException) {
            Assert.assertEquals("Cannot find extension point" +
                    " ${ExtensionPointProviderTest::class.java.name}\$", e.message)
        }
    }

    @Test
    fun testNonSamExtensionPointInterfaceProvider() {
        try {
            NonSamExtensionPointInterfaceProvider().getExtensionPoint()
            Assert.fail()
        } catch (e: YanwteException) {
            Assert.assertEquals("A SAM interface is required for extension point" +
                    " ${ExtensionPointProviderTest::class.java.name}\$NonSamExtensionPointInterface", e.message)
        }
    }

    interface TestExtensionPoint2 {
        fun foo()
    }

    class TestExtensionPointProvider2 : ExtensionPointProvider() {
        override fun tree(): Combinator {
            return extOfClassName("nonExistentExt")
        }

        override protected val isFailOnExtensionNotFound = false
    }

    interface TestExtensionPoint {
        fun foo()
    }

    class TestExtensionPointProvider : ExtensionPointProvider() {
        override fun tree(): Combinator {
            return extOfClassName("nonExistentExt")
        }
    }

    class ProviderWhichHasNoExtensionPointInterface : ExtensionPointProvider() {
        override fun tree(): Combinator {
            return empty()
        }
    }

    class NonSamExtensionPointInterface

    class NonSamExtensionPointInterfaceProvider : ExtensionPointProvider() {
        override fun tree(): Combinator {
            return empty()
        }
    }
}