Yanwte stands for yet another way to extend. It's a library to help you extending your programs. This library is written in Kotlin, and is compatible with Java 6.

# Where Does It Come From?

Yanwte came out of the need from the inside of Alibaba inc., the trading department. It's online trading systems are using the framework called tradespi, which is the predecestor of Yanwte. 3 years since the inception of tradespi, I felt it's time to rewrite it and open-source it to the community, so that we can share a mature thought of how to extend a complex program to everyone.

After open-sourced our trading systems inside the corporation, the systems could deliver 60+ bug fixes/features a week. The biggest system of them, could deliver 20+ bug fixes/features per week.

# Licence

[Apache license 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Documentation

For documentations, please see the [wiki](https://github.com/winteryoung/yanwte/wiki).

# QA

If you have a question, you can mail to me: 513805252@qq.com.

# Performance

Tradespi has been tested with 3 tmall double 11 festivals up to 2016. I believe Yanwte can achieve the same quality. To ensure the performance, Yanwte doesn't have any reflection calls and method calls that are known time consuming (like `String.replace`) on the hot code execution path.
