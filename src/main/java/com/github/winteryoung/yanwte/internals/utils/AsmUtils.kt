package com.github.winteryoung.yanwte.internals.utils

import org.objectweb.asm.Type

internal fun Class<*>.toAsmType() = Type.getType(this)

internal fun Class<*>.toInternalName() = this.toAsmType().internalName

internal fun Class<*>.toDescriptor() = this.toAsmType().descriptor