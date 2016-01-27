package com.github.winteryoung.yanwte;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Kotlin supports method implementation code in interfaces while Java doesn't.
 * If you use Java, you have to use an abstract class as the base. This is that base.
 *
 * @author Winter Young
 * @since 2016/1/27
 */
public abstract class BaseDataExtensionPoint4J implements DataExtensionPoint {
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> T getDataExtension() {
        return (T) DataExtensionPoint.DefaultImpls.getDataExtension(this);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> T getDataExtension(@NotNull String extSpaceName) {
        Intrinsics.checkParameterIsNotNull(extSpaceName, "extSpaceName");
        return (T) DataExtensionPoint.DefaultImpls.getDataExtension(this, extSpaceName);
    }
}
