package apiAcceptanceTests.extensionPoint;

import com.github.winteryoung.yanwte.ExtensionPointBuilder;
import kotlin.jvm.functions.Function1;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Winter Young
 * @since 2016/1/24
 */
public class ExtensionPointBuilderJavaTest {
    @Test
    public void testEmptyApiAcceptance() {
        new ExtensionPointBuilder<TestExtensionPoint>(TestExtensionPoint.class) {{
            setTree(empty());
        }};
    }

    @Test
    public void testExtApiAcceptance() {
        new ExtensionPointBuilder<TestExtensionPoint>(TestExtensionPoint.class) {{
            setTree(extOfClass(TestExtension.class));
        }};
    }

    @Test
    public void testChainApiAcceptance() {
        new ExtensionPointBuilder<TestExtensionPoint>(TestExtensionPoint.class) {{
            setTree(chain(
                    extOfClass(TestExtension.class)
            ));
        }};
    }

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    @Test
    public void testMapReduceApiAcceptance() {
        new ExtensionPointBuilder<TestExtensionPoint>(TestExtensionPoint.class) {{
            setTree(mapReduce(
                    Arrays.asList(
                            extOfClass(TestExtension.class)
                    ), new Function1<List<?>, Object>() {
                        @Override
                        public Object invoke(List<?> outputs) {
                            int sum = 0;
                            for (Object output : outputs) {
                                sum += (Integer) output;
                            }
                            return sum;
                        }
                    }));
        }};
    }
}
