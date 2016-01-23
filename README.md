Yanwte stands for yet another way to extend. It's a library to help you extending your programs. This library is written in Kotlin, and is compatible with JDK 1.6.

# Installation

Maven:

Name|Value
---|---
Group ID|com.github.winteryoung
Artifact ID|yanwte
Version|1.0.0

# Licence

[Apache license 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Introduction

In business programs, we typically face the problem of extending existing programs. To extend your program, you have to have a point to extend from. The standard way is to define such a point as an interface, called extension point. Say we have an extension point that needs to calculate the maximum quantity that a person can buy.

```java
public interface BuyQuantityLimit {
    Integer getQuantity(Context context, Merchandise merchandise, User buyer);
}
```

You may choose an implementation that returns the default value, let's call it an extension.

```java
class DefaultBuyQuantity implements BuyQuantityLimit {
    public Integer getQuantity(Context context, Merchandise merchandise, User buyer) {
        return Integer.MAX_VALUE;
    }
}
```

Or you may choose an extension that returns the quantity from a service.

```java
class BuyQuantityFromService implements BuyQuantityLimit {
    // some RPC service
    private BuyQuantityLimitService service;
  
    public Integer getQuantity(Context context, Merchandise merchandise, User buyer) {
        if (context....) {
            return service.getQuantity(merchandise.toDTO(), buyer.toDTO())
        }
        return null;
    }
}
```

The traditional way is to choose an extension based on the current runtime context. This process is done by each extension itself. The way it works is exactly the way the chain of reponsibility works.

Kotlin
```kotlin
ExtensionPointBuilder(BuyQuantityLimit::class.java).apply {
    tree = chain(
            extOfClass(BuyQuantityFromService::class.java),
            extOfClass(DefaultBuyQuantity::class.java)
    )
}.buildAndRegister()

val buyQuantityLimit = YanwteContainer.getExtensionPointByClass(BuyQuantityLimit::class.java)!!
val quantity = buyQuantityLimit.getQuantity(context, merchandise, buyer)
```

Java
```java
new ExtensionPointBuilder(BuyQuantityLimit.class) {{
    setTree(chain(
            extOfClass(BuyQuantityFromService.class),
            extOfClass(DefaultBuyQuantity.class)
    ));
}}.buildAndRegister();

BuyQuantityLimit buyQuantityLimit = YanwteContainer.INSTANCE.getExtensionPointByClass(BuyQuantityLimit.class);
int quantity = buyQuantityLimit.getQuantity(context, merchandise, buyer);
```

If `BuyQuantityLimitFromService` returns a non-null value, `buyQuantityLimit` returns that value. If `BuyQuantityLimitFromService` returns null, `buyQuantityLimit` returns the value returned by `DefaultBuyQuantity`. Like chain of reponsibility, it has the effect of short-circuit.

`chain` is just one of the combinators. Other combinators like `mapReduce` enables you to do all kinds of compositions to extensions. If you want some combinator that doesn't appear in Yanwte, [you can define your own](CustomTreeCombinator). Refer to [combintors](combinators) to see the complete list of embedded combinators. In essence, these combinators can form a single combinator that is arbitrarily complex to suit your needs.
