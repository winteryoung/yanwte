Yanwte stands for yet another way to extend. It's a library to help you extending your programs. This library is written in Kotlin, and is compatible with JDK 1.6.

# Installation

Maven:

Group ID|Artifact ID|Version
----|----|----
com.github.winteryoung|yanwte|1.0

# Licence

[Apache license 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Introduction

In business programs, we typically face the problem of extending existing programs. To extend your program, you have to have a point to extend from. The standard way is to define such a point as an interface. Say we have a point that needs to calculate the maximum quantity that a person can buy.

```
public interface BuyQuantityLimit {
  int getUnitPrice(Merchandise merchandise, User buyer);
}
```

You may choose an implementation that returns the default value.

```
class UnitPriceExtension implements UnitPriceExtensionPoint {
  Money getUnitPrice(Merchandise merchandise, User buyer) {
    return Integer.MAX_VALUE;
  }
}
```

Or you may choose an implementation that returns the quantity from a service.

```
class UnitPriceWithFeeExtension implements UnitPriceExtensionPoint {

  Money getUnitPrice(Merchandise merchandise, User buyer) {
    return
  }
}
```



The traditional way is to choose an implementation based on the current runtime context. What if you cannot choose a single implementation to do all the task, what if they need to work together? To demonstrate, let's say there is another implementation that constrains, the merchandise being sold on our platform cannot be dearer than $5000. (OK, this constraint example may not be appropriate. It's more suitable for a timeout scenario, but you get the point.)

```
class UnitPriceConstraint implements UnitPriceExtensionPoint {
  Money getUnitPrice(Merchandise merchandise, User buyer) {
    
  }
}
