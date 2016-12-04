package springtest

import com.github.winteryoung.yanwte.spring.AutowireProvider
import com.github.winteryoung.yanwte.spring.internals.YanwteSpringConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import springtest.biz.fixed.FixedNumBiz
import springtest.spi.NumberFormatter
import springtest.spi.NumberProcessor

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(SpringTest::class, YanwteSpringConfig::class))
@ComponentScan
class SpringTest {
    // the bean name must be the pattern [beanNameForInterface] + "Provider"
    // otherwise there will be too many beans matching this type
    @AutowireProvider
    private lateinit var numberProcessorProvider: NumberProcessor

    @AutowireProvider
    private lateinit var numberFormatterProvider: NumberFormatter

    @Test
    fun testEven() {
        val i = numberProcessorProvider.processInt(4)
        val text = numberFormatterProvider.format(i)
        Assert.assertEquals("Odd 3", text)
    }

    @Test
    fun testOdd() {
        val i = numberProcessorProvider.processInt(5)
        val text = numberFormatterProvider.format(i)
        Assert.assertEquals("Even 6", text)
    }

    @Test
    fun testMinimumValue() {
        val i = numberProcessorProvider.processInt(FixedNumBiz.FIXED_VALUE - 2)
        Assert.assertEquals(FixedNumBiz.FIXED_VALUE, i)
    }
}