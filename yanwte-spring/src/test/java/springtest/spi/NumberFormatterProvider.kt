package springtest.spi

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointProvider
import springtest.biz.even.EvenNumberBiz

/**
 * @author Winter Young
 * @since 2016/11/7
 */
class NumberFormatterProvider : ExtensionPointProvider() {
    override fun tree(): Combinator {
        // the order here really doesn't matter for this case,
        // because odd and even don't conflict. however,
        // if you need order, chain is suitable for you.
        // for other combinators, refer to the wiki.
        return chain(
                extOfClassName("springtest.biz.odd.OddNumberBiz"),
                extOfClass(EvenNumberBiz::class.java)
        )
    }
}
