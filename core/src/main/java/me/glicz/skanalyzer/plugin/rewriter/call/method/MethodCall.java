package me.glicz.skanalyzer.plugin.rewriter.call.method;

public record MethodCall(int opcode, String owner, String name, String descriptor, boolean isInterface) {
}
