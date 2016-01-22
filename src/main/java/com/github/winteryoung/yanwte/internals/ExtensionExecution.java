package com.github.winteryoung.yanwte.internals;

import com.github.winteryoung.yanwte.ExtensionPointInput;
import com.github.winteryoung.yanwte.ExtensionPointOutput;

/**
 * This interface could be replaced by a function type logically.
 * But in order to do bytecode instrumentation, we have to define
 * it using an interface.
 *
 * @author Winter Young
 * @since 2016/1/22
 */
public interface ExtensionExecution {
    /**
     * Execute the extension logic.
     */
    ExtensionPointOutput execute(ExtensionPointInput input);
}
