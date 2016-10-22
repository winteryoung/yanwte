package springtest;

import org.springframework.stereotype.Component;

@Component
public class DefaultBuyQuantity implements BuyQuantityLimit {
    public Integer getQuantity(Context context, Merchandise merchandise, User buyer) {
        return 5;
    }
}