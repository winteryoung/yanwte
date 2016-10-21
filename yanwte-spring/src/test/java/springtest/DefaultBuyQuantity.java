package springtest;

import org.springframework.stereotype.Component;

@Component
class DefaultBuyQuantity implements BuyQuantityLimit {
    public Integer getQuantity(Context context, Merchandise merchandise, User buyer) {
        return 5;
    }
}