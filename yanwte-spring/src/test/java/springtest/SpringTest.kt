package springtest

import com.github.winteryoung.yanwte.YanwteContainer
import org.junit.Assert
import org.junit.Test
import org.springframework.context.support.ClassPathXmlApplicationContext

class SpringTest {
    @Test
    fun test() {
        ClassPathXmlApplicationContext("yanwte.xml").start()

        val buyQuantityLimit = YanwteContainer.getExtensionPointByClass(BuyQuantityLimit::class.java)!!
        val quantity = buyQuantityLimit.getQuantity(Context(), Merchandise(), User())

        Assert.assertEquals(5, quantity)
    }
}