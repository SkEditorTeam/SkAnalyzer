package me.glicz.skanalyzer.bridge.util;

import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SetUtils {
    private SetUtils() {
    }

    public static <T, R> Set<R> transformSet(Set<T> set, Function<T, @Nullable R> mapper, Predicate<@Nullable R> predicate) {
        return set.stream().map(mapper).filter(predicate).collect(Collectors.toSet());
    }
}
