package springtest.biz.odd

import org.springframework.stereotype.Component
import springtest.spi.NumberFormatter
import springtest.spi.NumberProcessor

/**
 * @author Winter Young
 * @since 2016/10/23
 */
@Component
class OddNumberBiz : NumberProcessor, NumberFormatter {
    override fun processInt(i: Int?): Int? {
        if (i != null && i % 2 != 0) {
            return i + 1
        }

        // we cannot deal with it, let others do the work
        return null
    }

    override fun format(num: Int?): String? {
        if (num != null && num % 2 != 0) {
            return "Odd " + num
        }
        return null
    }
}
