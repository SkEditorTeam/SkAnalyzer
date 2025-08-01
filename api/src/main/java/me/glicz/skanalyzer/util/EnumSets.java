package me.glicz.skanalyzer.util;

import java.util.EnumSet;

public final class EnumSets {
    private EnumSets() {
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumSet<T> of(Class<T> clazz, T... values) {
        return values.length > 0 ? EnumSet.of(values[0], values) : EnumSet.noneOf(clazz);
    }
}
