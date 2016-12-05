package springtest.biz.even

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import springtest.services.EvenNumberService
import springtest.spi.NumberFormatter
import springtest.spi.NumberProcessor

/**
 * @author Winter Young
 * @since 2016/10/23
 */
@Component
class EvenNumberBiz : NumberProcessor, NumberFormatter {
    @Autowired
    private lateinit var evenNumberService: EvenNumberService

    override fun processInt(i: Int?): Int? {
        return evenNumberService.processInt(i)
    }

    override fun format(num: Int?): String? {
        if (num != null && num % 2 == 0) {
            return "Even " + num
        }
        return null
    }
}
