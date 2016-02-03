package springtest;

import com.github.winteryoung.yanwte.Combinator;
import com.github.winteryoung.yanwte.ExtensionPointProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author Winter Young
 */
public class BuyQuantityLimitProvider extends ExtensionPointProvider {
    @NotNull
    @Override
    protected Combinator tree() {
        return extOfClass(DefaultBuyQuantity.class);
    }
}
