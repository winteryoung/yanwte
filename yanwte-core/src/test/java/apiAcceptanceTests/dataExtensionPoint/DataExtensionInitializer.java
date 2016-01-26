package apiAcceptanceTests.dataExtensionPoint;

import com.github.winteryoung.yanwte.DataExtensionPoint;
import com.github.winteryoung.yanwte.YanwteDataExtensionInitializer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Winter Young
 * @since 2016/1/25
 */
public class DataExtensionInitializer extends YanwteDataExtensionInitializer {
    @NotNull
    @Override
    public Object initialize(@NotNull DataExtensionPoint dataExtensionPoint) {
        if (dataExtensionPoint instanceof TestData) {
            TestData testData = (TestData) dataExtensionPoint;
            return new TestDataExt(testData.getI());
        }
        throw new RuntimeException();
    }
}
