package com.github.winteryoung.yanwte.internals.bytecode

import com.github.winteryoung.yanwte.ExtensionPointInput
import com.github.winteryoung.yanwte.ExtensionPointOutput
import com.github.winteryoung.yanwte.YanwteException
import com.github.winteryoung.yanwte.internals.ExtensionPoint
import com.github.winteryoung.yanwte.internals.utils.toDescriptor
import com.github.winteryoung.yanwte.internals.utils.toInternalName
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import java.lang.reflect.Method

/*
This function is effectively the same as

public class ExtensionPointTestProxy implements TestExtensionPoint {
  private ExtensionPoint ep;

  public ExtensionPointTestProxy(ExtensionPoint ep) {
    this.ep = ep;
  }

  public Integer foo(Integer arg0, Integer arg1) {
    ArrayList args = new ArrayList();
    args.add(arg0);
    args.add(arg1);
    ExtensionPointOutput output = this.ep.invoke(new ExtensionPointInput(args));
    return (Integer) output.getReturnValue();
  }
}
 */
/**
 * Generates proxy class for the given extension point. This proxy eliminates the need for
 * reflective invocation. Basically, it passes all arguments from [extensionPointInterfaceClass]
 * to [extensionPoint], and passes the return value from [extensionPoint] to [extensionPointInterfaceClass].
 */
internal fun <T> generateExtensionPointProxy(
        extensionPoint: ExtensionPoint,
        extensionPointInterfaceClass: Class<T>
): T {
    if (extensionPoint.samInterface != extensionPointInterfaceClass) {
        throw IllegalArgumentException()
    }

    val (name, bytes) = generateExtensionPointProxyBytes(extensionPoint, extensionPointInterfaceClass)
    return loadExtensionPointProxyInstance(name, bytes, extensionPoint)
}

private fun <T> generateExtensionPointProxyBytes(
        extensionPoint: ExtensionPoint,
        extensionPointInterfaceClass: Class<T>
): Pair<String, ByteArray> {
    val extensionPointClass = ExtensionPoint::class.java
    val proxyMethod = extensionPoint.method
    val generatedType = Type.getObjectType("${extensionPointInterfaceClass.toInternalName()}__yanwteProxy__")

    checkProxyMethodTypeIntegrity(proxyMethod, extensionPointInterfaceClass)

    val cw = ClassWriter(COMPUTE_MAXS)
    cw.visit(
            V1_6,
            ACC_PUBLIC + ACC_SUPER,
            generatedType.internalName,
            null,
            "java/lang/Object",
            arrayOf(extensionPointInterfaceClass.toInternalName())
    )

    buildFields(cw, extensionPointClass)
    buildConstructor(cw, extensionPointClass, generatedType)
    buildProxyMethod(cw, proxyMethod, extensionPointClass, generatedType)

    cw.visitEnd()

    return "${extensionPointInterfaceClass.name}__yanwteProxy__" to cw.toByteArray()
}

private fun buildProxyMethod(
        cw: ClassWriter,
        proxyMethod: Method,
        extensionPointClass: Class<ExtensionPoint>,
        generatedType: Type
) {
    val proxyMethodParamsSig = proxyMethod.parameterTypes.map {
        it.toDescriptor()
    }.joinToString("")
    val proxyMethodSig = "($proxyMethodParamsSig)${proxyMethod.returnType.toDescriptor()}"
    val (argsVarIndex, outputVarIndex) = proxyMethod.parameters.size.let { offset ->
        listOf(offset + 1, offset + 2)
    }
    val extensionPointInputClass = ExtensionPointInput::class.java
    val extensionPointOutputClass = ExtensionPointOutput::class.java

    cw.visitMethod(ACC_PUBLIC, proxyMethod.name, proxyMethodSig, null, null).run {
        visitCode()

        val l0 = Label() // args = ArrayList()
        visitLabel(l0)
        visitTypeInsn(NEW, "java/util/ArrayList")
        visitInsn(DUP)
        visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
        visitVarInsn(ASTORE, argsVarIndex)

        val l1 = Label() // args.add(param)
        visitLabel(l1)
        proxyMethod.parameterTypes.forEachIndexed { i, paramType ->
            visitVarInsn(ALOAD, argsVarIndex)
            visitVarInsn(ALOAD, i + 1)
            visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
            visitInsn(POP)
        }

        // prepare this.ep
        visitVarInsn(ALOAD, 0)
        visitFieldInsn(
                GETFIELD,
                generatedType.internalName,
                "ep",
                extensionPointClass.toDescriptor()
        )

        // new ExtensionPointInput(args)
        visitTypeInsn(NEW, extensionPointInputClass.toInternalName())
        visitInsn(DUP)
        visitVarInsn(ALOAD, argsVarIndex)
        visitMethodInsn(
                INVOKESPECIAL,
                extensionPointInputClass.toInternalName(),
                "<init>",
                "(Ljava/util/List;)V",
                false
        )

        // ep.invoke(extensionPointInput)
        visitMethodInsn(
                INVOKEVIRTUAL,
                extensionPointClass.toInternalName(),
                "invoke",
                "(${extensionPointInputClass.toDescriptor()})${extensionPointOutputClass.toDescriptor()}",
                false
        )

        // val output = ep.invoke(ExtensionPointInput(args))
        visitVarInsn(ASTORE, outputVarIndex)
        visitVarInsn(ALOAD, outputVarIndex)

        // output.returnValue
        visitMethodInsn(
                INVOKEVIRTUAL,
                extensionPointOutputClass.toInternalName(),
                "getReturnValue",
                "()Ljava/lang/Object;",
                false
        )

        // return output.returnValue
        if (proxyMethod.returnType == Void.TYPE) {
            visitInsn(RETURN)
        } else {
            visitTypeInsn(CHECKCAST, proxyMethod.returnType.toInternalName())
            visitInsn(ARETURN)
        }

        // define local variables
        val l2 = Label()
        visitLabel(l2)
        visitLocalVariable("this", generatedType.descriptor, null, l0, l2, 0)

        // define arguments
        proxyMethod.parameterTypes.forEachIndexed { i, paramType ->
            visitLocalVariable("arg$i", paramType.toDescriptor(), null, l0, l2, i + 1)
        }

        visitLocalVariable(
                "args",
                "Ljava/util/ArrayList;",
                "Ljava/util/ArrayList<Ljava/lang/Object;>;",
                l1,
                l2,
                argsVarIndex
        )

        visitLocalVariable(
                "output",
                extensionPointOutputClass.toDescriptor(),
                null,
                l1,
                l2,
                outputVarIndex
        )

        visitMaxs(0, 0)
        visitEnd()
    }
}

fun checkProxyMethodTypeIntegrity(proxyMethod: Method, extensionPointInterfaceClass: Class<*>) {
    proxyMethod.parameterTypes.toArrayList().apply { add(proxyMethod.returnType) }.forEach { type ->
        if (type.isPrimitive && type != Void.TYPE) {
            throw YanwteException("Primitives are not supported: ${extensionPointInterfaceClass.name}")
        }
    }
}

/**
 * this.ep = ep
 */
private fun buildConstructor(
        cw: ClassWriter,
        extensionPointClass: Class<ExtensionPoint>,
        generatedType: Type
) {
    cw.visitMethod(
            ACC_PUBLIC,
            "<init>",
            "(${extensionPointClass.toDescriptor()})V",
            null,
            null
    ).run {
        visitCode()
        val l0 = Label()
        visitLabel(l0)
        visitVarInsn(ALOAD, 0)
        visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        val l1 = Label()
        visitLabel(l1)
        visitVarInsn(ALOAD, 0)
        visitVarInsn(ALOAD, 1)
        visitFieldInsn(
                PUTFIELD,
                generatedType.internalName,
                "ep",
                extensionPointClass.toDescriptor()
        )
        val l2 = Label()
        visitLabel(l2)
        visitInsn(RETURN)
        val l3 = Label()
        visitLabel(l3)
        visitLocalVariable("this", generatedType.descriptor, null, l0, l3, 0)
        visitLocalVariable("ep", extensionPointClass.toDescriptor(), null, l0, l3, 1)
        visitMaxs(0, 0)
        visitEnd()
    }
}

private fun buildFields(cw: ClassWriter, extensionPointClass: Class<ExtensionPoint>) {
    cw.visitField(
            ACC_PRIVATE,
            "ep",
            extensionPointClass.toDescriptor(),
            null,
            null
    ).run {
        visitEnd()
    }
}