package integrationTests.mixedTest;

import com.github.winteryoung.yanwte.Combinator
import com.github.winteryoung.yanwte.ExtensionPointProvider
import integrationTests.mixedTest.extspace1.ExtensionSpace1
import integrationTests.mixedTest.extspace2.TestExtension2

class TestExtensionPointProvider : ExtensionPointProvider() {
    override fun tree(): Combinator {
        return chain(
                extOfExtSpace(ExtensionSpace1::class.java),
                extOfClass(TestExtension2::class.java)
        )
    }
}