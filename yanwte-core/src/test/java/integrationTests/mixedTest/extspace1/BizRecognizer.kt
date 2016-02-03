package integrationTests.mixedTest.extspace1

import com.github.winteryoung.yanwte.YanwteBizRecognizer
import integrationTests.mixedTest.TestData
import integrationTests.mixedTest.TestDataExt

/**
 * @author Winter Young
 */
class BizRecognizer : YanwteBizRecognizer {
    override fun recognizes(domainObject: Any): Boolean {
        return when(domainObject) {
            is TestData -> {
                domainObject.getDataExtension<TestDataExt>()?.let { ext ->
                    val (i) = ext
                    i % 2 == 0
                } ?: false
            }
            else -> {
                false
            }
        }
    }
}