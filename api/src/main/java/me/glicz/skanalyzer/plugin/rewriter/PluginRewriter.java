package me.glicz.skanalyzer.plugin.rewriter;

import com.google.common.collect.ImmutableSet;
import me.glicz.skanalyzer.plugin.rewriter.call.method.MaterialValuesRewriter;
import me.glicz.skanalyzer.plugin.rewriter.call.method.MethodCall;
import org.objectweb.asm.*;

import java.util.Set;

public class PluginRewriter {
    private static final Set<MethodCall.Rewriter> METHOD_CALL_REWRITERS;

    static {
        METHOD_CALL_REWRITERS = ImmutableSet.<MethodCall.Rewriter>builder()
                .add(MaterialValuesRewriter.INSTANCE)
                .build();
    }

    public static byte[] rewrite(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(ClassReader.EXPAND_FRAMES);
        ClassVisitor classVisitor = new ClassRewriter(classWriter);

        classReader.accept(classVisitor, 0);

        return classWriter.toByteArray();
    }

    static class ClassRewriter extends ClassVisitor {
        public ClassRewriter(ClassWriter classWriter) {
            super(Opcodes.ASM9, classWriter);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new MethodRewriter(super.visitMethod(access, name, descriptor, signature, exceptions));
        }
    }

    static class MethodRewriter extends MethodVisitor {
        public MethodRewriter(MethodVisitor methodVisitor) {
            super(Opcodes.ASM9, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            MethodCall methodCall = new MethodCall(opcode, owner, name, descriptor, isInterface);

            for (MethodCall.Rewriter rewriter : METHOD_CALL_REWRITERS) {
                if (!rewriter.test(methodCall)) {
                    continue;
                }

                MethodCall rewrittenCall = rewriter.rewrite(methodCall);
                super.visitMethodInsn(
                        rewrittenCall.opcode(),
                        rewrittenCall.owner(),
                        rewrittenCall.name(),
                        rewrittenCall.descriptor(),
                        rewrittenCall.isInterface()
                );
                return;
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
