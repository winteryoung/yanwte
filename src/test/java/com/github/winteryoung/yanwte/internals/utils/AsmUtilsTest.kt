package com.github.winteryoung.yanwte.internals.utils

import com.github.winteryoung.yanwte.internals.ExtensionPoint
import org.junit.Assert
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/22
 */
class AsmUtilsTest {
    @Test
    fun testToInternalName() {
        Assert.assertEquals(
                "com/github/winteryoung/yanwte/internals/ExtensionPoint",
                ExtensionPoint::class.java.toInternalName()
        )
    }

    @Test
    fun testToDescriptor() {
        Assert.assertEquals(
                "Lcom/github/winteryoung/yanwte/internals/ExtensionPoint;",
                ExtensionPoint::class.java.toDescriptor()
        )
    }
}