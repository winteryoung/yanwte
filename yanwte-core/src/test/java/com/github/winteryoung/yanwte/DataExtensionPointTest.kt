package com.github.winteryoung.yanwte

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/26
 */
class DataExtensionPointTest {
    @Before
    fun before() {
        YanwteContainer.clear()
    }

    @After
    fun after() {
        YanwteContainer.clear()
    }

    @Test
    fun testGetDataExtensionWhenNoDataExtInitializerDefined() {
        val extSpace = getDataExtInitializer("testExtSpace") {
            null
        }

        Assert.assertEquals(EmptyDataExtensionInitializer, extSpace)
    }
}