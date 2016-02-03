package apiAcceptanceTests.extensionPointProvider

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointProvider
import org.junit.Test

/**
 * @author Winter Young
 */
class ExtensionPointProviderTest {
    @Test
    fun testEmptyApiAcceptance() {
        object : ExtensionPointProvider() {
            override fun tree(): Combinator {
                return empty()
            }
        }
    }

    @Test
    fun testExtApiAcceptance() {
        object : ExtensionPointProvider() {
            override fun tree(): Combinator {
                return extOfClass(TestExtension::class.java)
            }
        }
    }

    @Test
    fun testChainApiAcceptance() {
        object : ExtensionPointProvider() {
            override fun tree(): Combinator {
                return chain(
                        extOfClass(TestExtension::class.java)
                )
            }
        }
    }

    @Test
    fun testMapReduceApiAcceptance() {
        object : ExtensionPointProvider() {
            override fun tree(): Combinator {
                return mapReduce<Int>(listOf(
                        extOfClass(TestExtension::class.java)
                )) { outputs ->
                    outputs.fold(0) { acc, next ->
                        acc + next
                    }
                }
            }
        }
    }

    interface TestExtensionPoint {
        fun test(a: Int): Int
    }

    class TestExtension : TestExtensionPoint {
        override fun test(a: Int): Int {
            return a
        }
    }
}
