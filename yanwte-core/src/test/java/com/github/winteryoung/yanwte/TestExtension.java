package com.github.winteryoung.yanwte;

public class TestExtension implements TestExtensionPoint {
    @Override
    public int test(int a) {
        return a;
    }
}