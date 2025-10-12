package me.glicz.skanalyzer.plugin.rewriter.call.method;

import me.glicz.skanalyzer.plugin.rewriter.Rewriter;
import me.glicz.skanalyzer.plugin.rewriter.call.field.ParticleConstantsRewriter;
import me.glicz.skanalyzer.util.Booleans;
import org.bukkit.Particle;
import org.objectweb.asm.Opcodes;

public class ParticleValueOfRewriter implements Rewriter<MethodCall> {
    public static final ParticleValueOfRewriter INSTANCE = new ParticleValueOfRewriter();

    private static final MethodCall METHOD_CALL = new MethodCall(
            Opcodes.INVOKESTATIC,
            ParticleValueOfRewriter.class.getName().replace('.', '/'),
            "valueOf",
            "(Ljava/lang/String;)Lorg/bukkit/Particle;",
            false
    );

    private ParticleValueOfRewriter() {
    }

    public static Particle valueOf(String name) {
        return Particle.valueOf(ParticleConstantsRewriter.rewrite(name));
    }

    @Override
    public MethodCall rewrite(MethodCall call) {
        return METHOD_CALL;
    }

    @Override
    public boolean test(MethodCall call) {
        return Booleans.and(
                call.opcode() == Opcodes.INVOKESTATIC,
                call.owner().equals("org/bukkit/Particle"),
                call.name().equals("valueOf")
        );
    }
}
