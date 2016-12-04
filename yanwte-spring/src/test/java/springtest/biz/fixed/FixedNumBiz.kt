package springtest.biz.fixed

import org.springframework.stereotype.Component
import springtest.spi.NumberProcessor

/**
 * @author Winter Young
 * @since 2016/12/4
 */
@Component
class FixedNumBiz : NumberProcessor {
    override fun processInt(i: Int?): Int? {
        return FIXED_VALUE
    }

    companion object {
        val FIXED_VALUE = 3
    }
}