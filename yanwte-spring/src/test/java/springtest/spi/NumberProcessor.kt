package springtest.spi

import com.github.winteryoung.yanwte.spring.YanwteExtensionPoint

/**
 * @author Winter Young
 * @since 2016/10/23
 */
@YanwteExtensionPoint
interface NumberProcessor {
    // params are required to be boxed type. the return type can be void or boxed type
    fun processInt(i: Int?): Int?
}
