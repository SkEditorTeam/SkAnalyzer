package me.glicz.skanalyzer.plugin.rewriter.call.method;

import me.glicz.skanalyzer.util.Booleans;
import org.bukkit.Material;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

import static java.util.function.Predicate.not;

public final class MaterialValuesRewriter implements MethodCall.Rewriter {
    public static final MaterialValuesRewriter INSTANCE = new MaterialValuesRewriter();

    private static final MethodCall METHOD_CALL = new MethodCall(
            Opcodes.INVOKESTATIC,
            MaterialValuesRewriter.class.getName().replace('.', '/'),
            "values",
            "()[Lorg/bukkit/Material;",
            false
    );
    private static final Material[] MATERIALS = Arrays.stream(Material.values())
            .filter(not(Material::isLegacy))
            .toArray(Material[]::new);

    private MaterialValuesRewriter() {
    }

    public static Material[] values() {
        return MATERIALS.clone();
    }

    @Override
    public MethodCall rewrite(MethodCall call) {
        return METHOD_CALL;
    }

    @Override
    public boolean test(MethodCall call) {
        return Booleans.and(
                call.opcode() == Opcodes.INVOKESTATIC,
                call.owner().equals("org/bukkit/Material"),
                call.name().equals("values")
        );
    }
}
