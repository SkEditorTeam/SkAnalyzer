package me.glicz.skanalyzer.util.function;

import java.util.function.BiPredicate;

public final class BiPredicates {
    private BiPredicates() {
    }

    public static <T, U> BiPredicate<U, T> reversed(BiPredicate<T, U> predicate) {
        return (u, t) -> predicate.test(t, u);
    }
}
