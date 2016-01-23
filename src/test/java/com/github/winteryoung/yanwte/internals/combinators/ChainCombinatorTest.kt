package com.github.winteryoung.yanwte.internals.combinators

import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.internals.ExtensionExecution
import com.github.winteryoung.yanwte.internals.YanwteExtension
import org.junit.Assert
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/19
 */
class ChainCombinatorTest {
    @Test
    fun test() {
        val ext1 = buildExtension { i ->
            if (i % 2 == 0) i + 1 else null
        }
        val ext2 = buildExtension { i ->
            if (i % 2 != 0) i - 1 else null
        }

        val node = ChainCombinator("testExtPoint", listOf(
                buildExtNode(extension = ext1),
                buildExtNode(extension = ext2)
        ))

        val (output1) = node(ExtensionPointInput(listOf(3)))
        Assert.assertEquals(2, output1)

        val (output2) = node(ExtensionPointInput(listOf(4)))
        Assert.assertEquals(5, output2)
    }

    private fun buildExtension(
            extensionName: String = "testExt",
            action: (Int) -> Int?
    ): YanwteExtension {
        return YanwteExtension(extensionName, ExtensionExecution { input ->
            val (args) = input
            val (arg) = args
            val i = arg as Int
            ExtensionPointOutput(action(i))
        })
    }

    private fun buildExtNode(
            extensionPointName: String = "testExtPoint",
            extension: YanwteExtension
    ) = LeafExtensionCombinator(extensionPointName, extension)
}