package apiAcceptanceTests.extensionPointProvider;

import com.github.winteryoung.yanwte.Combinator;
import com.github.winteryoung.yanwte.ExtensionPointProvider;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author Winter Young
 */
public class ExtensionPointProviderJavaTest {
    @Test
    public void testMapReduceApiAcceptance() {
        new ExtensionPointProvider() {
            @NotNull
            @Override
            protected Combinator tree() {
                return mapReduce(
                        Collections.singletonList(
                                extOfClass(TestExtension.class)
                        ), outputs -> {
                            int sum = 0;
                            for (Object output : outputs) {
                                sum += (Integer) output;
                            }
                            return sum;
                        });
            }

            @Override
            protected boolean isFailOnExtensionNotFound() {
                return false;
            }
        };
    }
}
