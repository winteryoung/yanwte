package springtest.spi

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointProvider
import springtest.biz.even.Even
import springtest.biz.even.EvenNumberBiz
import springtest.biz.odd.OddNumberBiz

/**
 * Provider of [NumberProcessor].

 * @author Winter Young
 * @since 2016/10/23
 */
class NumberProcessorProvider : ExtensionPointProvider() {
    override fun tree(): Combinator {
        return mapReduce<Int>(
                listOf(
                        chain(
                                extOfExtSpaceName("springtest.biz.odd"),
                                extOfExtSpace(Even::class.java)
                        ),
                        extOfExtSpaceName("springtest.biz.fixed")
                ),
                { elements ->
                    elements.max()!!
                }
        )
    }
}
