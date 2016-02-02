package apiAcceptanceTests.extensionPointBuilder;

public class TestExtension implements TestExtensionPoint {
    @Override
    public int test(int a) {
        return a;
    }
}