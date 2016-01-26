package apiAcceptanceTests.dataExtensionPoint;

import integrationTests.mixedTest.TestData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Winter Young
 * @since 2016/1/25
 */
public class TestExtension implements TestExtensionPoint {
    @Nullable
    @Override
    public String foo(@NotNull TestData testData) {
        TestDataExt testDataExt = (TestDataExt) testData.getDataExtension();
        if (testDataExt != null) {
            return testDataExt.getI() + "";
        } else {
            return null;
        }
    }
}
