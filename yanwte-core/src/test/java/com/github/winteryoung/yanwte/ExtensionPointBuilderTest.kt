package com.github.winteryoung.yanwte

import apiAcceptanceTests.extensionPointBuilder.ExtensionPointBuilderTest
import org.junit.Assert
import org.junit.Test

class ExtensionPointBuilderTest {
    @Test
    fun testExtOfClassNameFailingOnExtensionNotFound() {
        try {
            ExtensionPointBuilder(
                    ExtensionPointBuilderTest.TestExtensionPoint::class.java,
                    ExtensionPointBuilderOptions()
            ).apply {
                tree = chain(
                        extOfClassName("testExt")
                )
            }
            Assert.fail()
        } catch (e: YanwteException) {
            Assert.assertEquals("Cannot find extension POJO with name testExt", e.message)
        }
    }

    @Test
    fun testExtOfClassNameReturningEmptyCombinatorOnExtensionNotFound() {
        ExtensionPointBuilder(
                ExtensionPointBuilderTest.TestExtensionPoint::class.java,
                ExtensionPointBuilderOptions(
                        failOnExtensionNotFound = false
                )
        ).apply {
            tree = chain(
                    extOfClassName("testExt")
            )
        }.build().let {
            Assert.assertNotNull(it)
        }
    }
}
