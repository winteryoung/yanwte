package com.github.winteryoung.yanwte.internals.bytecode;

import com.github.winteryoung.yanwte.ExtensionPointInput;
import com.github.winteryoung.yanwte.ExtensionPointOutput;
import com.github.winteryoung.yanwte.internals.ExtensionExecution;

import java.util.List;

/**
 * @author Winter Young
 * @since 2016/1/22
 */
public class ExtensionExecutionProxy implements ExtensionExecution {
    public ExtensionExecutionProxy(TestExtensionPoint target) {
        this.target = target;
    }

    private TestExtensionPoint target;

    @Override
    public ExtensionPointOutput execute(ExtensionPointInput input) {
        List<Object> args = input.getArgs();
        target.foo((Integer) args.get(0), (Long) args.get(1));
        return new ExtensionPointOutput(null);
    }
}
