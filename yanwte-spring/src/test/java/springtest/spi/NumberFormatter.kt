package springtest.spi

import com.github.winteryoung.yanwte.spring.YanwteExtensionPoint

/**
 * @author Winter Young
 * @since 2016/11/7
 */
@YanwteExtensionPoint
interface NumberFormatter {
    fun format(num: Int?): String?
}
