package springtest.services

import org.springframework.stereotype.Component

/**
 * @author fanshen
 * @since 2016/12/5
 */
@Component
class EvenNumberService {
    fun processInt(i: Int?): Int? {
        if (i != null && i % 2 == 0) {
            return i - 1
        }

        // we cannot deal with it, let others do the work
        return null
    }
}
