package me.glicz.skanalyzer.plugin.rewriter;

import me.glicz.skanalyzer.plugin.rewriter.call.field.FieldCall;
import me.glicz.skanalyzer.plugin.rewriter.call.field.ParticleConstantsRewriter;
import me.glicz.skanalyzer.plugin.rewriter.call.method.MaterialValuesRewriter;
import me.glicz.skanalyzer.plugin.rewriter.call.method.MethodCall;
import me.glicz.skanalyzer.plugin.rewriter.call.method.ParticleValueOfRewriter;
import org.objectweb.asm.*;

import java.util.Set;

public class PluginRewriter {
    private static final Set<Rewriter<FieldCall>> FIELD_CALL_REWRITERS = Set.of(
            ParticleConstantsRewriter.INSTANCE
    );
    private static final Set<Rewriter<MethodCall>> METHOD_CALL_REWRITERS = Set.of(
            MaterialValuesRewriter.INSTANCE,
            ParticleValueOfRewriter.INSTANCE
    );

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
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            FieldCall call = new FieldCall(opcode, owner, name, descriptor);

            for (Rewriter<FieldCall> rewriter : FIELD_CALL_REWRITERS) {
                if (!rewriter.test(call)) {
                    continue;
                }

                FieldCall rewrittenCall = rewriter.rewrite(call);
                super.visitFieldInsn(
                        rewrittenCall.opcode(),
                        rewrittenCall.owner(),
                        rewrittenCall.name(),
                        rewrittenCall.descriptor()
                );
                return;
            }

            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            MethodCall call = new MethodCall(opcode, owner, name, descriptor, isInterface);

            for (Rewriter<MethodCall> rewriter : METHOD_CALL_REWRITERS) {
                if (!rewriter.test(call)) {
                    continue;
                }

                MethodCall rewrittenCall = rewriter.rewrite(call);
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
