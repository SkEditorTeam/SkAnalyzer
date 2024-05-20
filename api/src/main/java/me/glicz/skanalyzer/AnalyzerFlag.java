package me.glicz.skanalyzer;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum AnalyzerFlag {
    FORCE_VAULT_HOOK("--forceVaultHook"),
    FORCE_REGIONS_HOOK("--forceRegionsHook"),
    SKIP_EXTRACTING_ADDONS("--skipExtractingAddons"),
    ;

    private static final Map<String, AnalyzerFlag> ARG_TO_FLAG = new HashMap<>();

    static {
        for (AnalyzerFlag flag : values()) {
            ARG_TO_FLAG.put(flag.arg, flag);
        }
    }

    private final String arg;

    public static AnalyzerFlag getByArg(String arg) {
        return ARG_TO_FLAG.get(arg);
    }
}
