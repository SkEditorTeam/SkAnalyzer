package me.glicz.skanalyzer.plugin.rewriter.call.method;

import java.util.function.Predicate;

public record MethodCall(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    public interface Rewriter extends Predicate<MethodCall> {
        MethodCall rewrite(MethodCall call);
    }
}
