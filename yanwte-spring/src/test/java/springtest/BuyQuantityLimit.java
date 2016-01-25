package springtest;

public interface BuyQuantityLimit {
    Integer getQuantity(Context context, Merchandise merchandise, User buyer);
}