package me.glicz.skanalyzer.bridge.util;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static <T> T nonNull(@Nullable T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T, R extends @Nullable Object> R transformValue(@Nullable T value, Function<T, R> function) {
        return transformValue(value, function, null);
    }

    public static <T, R extends @Nullable Object> R transformValue(@Nullable T value, Function<T, R> function, R defaultValue) {
        return value != null ? function.apply(value) : defaultValue;
    }
}
