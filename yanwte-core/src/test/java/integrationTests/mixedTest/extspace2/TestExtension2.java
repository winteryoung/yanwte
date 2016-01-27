package integrationTests.mixedTest.extspace2;

import integrationTests.mixedTest.TestData;
import integrationTests.mixedTest.TestDataExt;
import integrationTests.mixedTest.TestExtensionPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Winter Young
 * @since 2016/1/27
 */
public class TestExtension2 implements TestExtensionPoint {
    @Nullable
    @Override
    public String foo(@NotNull TestData testData) {
        TestDataExt ext = (TestDataExt) testData.getDataExtension("integrationTests.mixedTest.extspace1");
        assert ext != null;
        int i = ext.getI();
        if (i % 2 != 0) {
            return i - 1 + "";
        }
        return null;
    }
}
