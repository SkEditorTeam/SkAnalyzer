package me.glicz.skanalyzer;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum AnalyzerFlag {
    FORCE_VAULT_HOOK("--forceVaultHook"),
    FORCE_REGIONS_HOOK("--forceRegionsHook"),
    ;

    private static final Map<String, AnalyzerFlag> ARG_TO_FLAG = new HashMap<>();

    static {
        for (AnalyzerFlag flag : values()) {
            ARG_TO_FLAG.put(flag.arg, flag);
        }
    }

    private final String arg;

    AnalyzerFlag(String arg) {
        this.arg = arg;
    }


    public static @Nullable AnalyzerFlag getByArg(String arg) {
        return ARG_TO_FLAG.get(arg);
    }
}
