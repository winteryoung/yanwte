package com.github.winteryoung.yanwte

import com.github.winteryoung.yanwte.internals.DefaultYanwtePlugin
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author Winter Young
 * @since 2016/1/21
 */
class YanwtePluginTest {
    @Before
    fun before() {
        YanwtePlugin.clearPluginRegistry()
    }

    @After
    fun after() {
        YanwtePlugin.clearPluginRegistry()
    }

    @Test
    fun testRegisterAndFind() {
        val plugin1 = TestPlugin1()
        YanwtePlugin.registerPlugin(plugin1, "a.b")
        val plugin2 = TestPlugin2()
        YanwtePlugin.registerPlugin(plugin2, "a.c")

        Assert.assertEquals(plugin1, YanwtePlugin.getPluginByExtensionName("a.b.c.Test"))
        Assert.assertEquals(plugin2, YanwtePlugin.getPluginByExtensionName("a.c.Test"))
        Assert.assertEquals(plugin1, YanwtePlugin.getPluginByExtensionName("a.b.Test"))
        Assert.assertTrue(YanwtePlugin.getPluginByExtensionName("a.Test") is DefaultYanwtePlugin)
    }

    abstract class TestPluginBase : YanwtePlugin {
        override fun getExtensionByName(extensionName: String): Any? {
            throw UnsupportedOperationException()
        }
    }

    class TestPlugin1 : TestPluginBase()
    class TestPlugin2 : TestPluginBase()
}