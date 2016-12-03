package integrationTests.mixedTest.extspace1

import com.github.winteryoung.yanwte.YanwteExtensionSpace

/**
 * @author Winter Young
 * @since 2016/1/26
 */
class ExtensionSpace1 : YanwteExtensionSpace() {
    override fun initializeImpl() {
        addFriendExtSpace("integrationTests.mixedTest.extspace2")
    }
}