package springtest;

import com.github.winteryoung.yanwte.YanwteExt;

@YanwteExt
class DefaultBuyQuantity implements BuyQuantityLimit {
    public Integer getQuantity(Context context, Merchandise merchandise, User buyer) {
        return 5;
    }
}