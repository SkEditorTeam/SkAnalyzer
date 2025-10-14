package me.glicz.skanalyzer.plugin.rewriter.call.method.oldenum;

import me.glicz.skanalyzer.plugin.rewriter.Rewriter;
import me.glicz.skanalyzer.plugin.rewriter.call.method.MethodCall;
import me.glicz.skanalyzer.util.Booleans;
import org.objectweb.asm.Opcodes;

public final class OldEnumValuesRewriter implements Rewriter<MethodCall> {
    public static final OldEnumValuesRewriter INSTANCE = new OldEnumValuesRewriter();

    private OldEnumValuesRewriter() {
    }

    @Override
    public MethodCall rewrite(MethodCall call) {
        return new MethodCall(Opcodes.INVOKESTATIC, call.owner(), call.name(), call.descriptor(), true);
    }

    @Override
    public boolean test(MethodCall call) {
        return Booleans.and(
                call.opcode() == Opcodes.INVOKESTATIC,
                OldEnums.OLD_ENUMS.contains(call.owner()),
                call.name().equals("values"),
                !call.isInterface()
        );
    }
}
