package me.glicz.skanalyzer.bridge.util;

import org.jspecify.annotations.Nullable;

public final class StringUtils {
    private StringUtils() {
    }

    public static @Nullable String emptyToNull(@Nullable String str) {
        return str == null || str.isEmpty() ? null : str;
    }
}
