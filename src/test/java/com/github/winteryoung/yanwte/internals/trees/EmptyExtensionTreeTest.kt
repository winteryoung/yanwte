package com.github.winteryoung.yanwte.internals.trees

import com.github.winteryoung.yanwte.ExtensionPointInput
import org.junit.Assert
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/19
 */
class EmptyExtensionTreeTest {
    @Test
    fun test() {
        val node = EmptyExtensionTree("testExtPoint")
        val (output) = node(ExtensionPointInput(listOf(3)))

        Assert.assertEquals(null, output)
    }
}