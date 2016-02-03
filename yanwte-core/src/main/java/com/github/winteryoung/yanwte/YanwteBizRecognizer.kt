package com.github.winteryoung.yanwte

/**
 * Multiple extensions in an extension space usually have similar predicates.
 * For example, if they all require the input, let' say an order line, has
 * the tag 49588. No need to write that predicate multiple times, we can extract
 * it into a class implementing this interface, placed in the package of the
 * extension space, named as `BizRecognizer`.
 *
 * @author Winter Young
 */
interface YanwteBizRecognizer {
    /**
     * Client implements this method tell Yanwte if your extension space
     * can recognize the given domain object. For unrecognizable objects,
     * extensions of your extension space won't be called.
     */
    fun recognizes(domainObject: Any): Boolean
}