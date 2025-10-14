package me.glicz.skanalyzer.util;

import java.util.Set;
import java.util.function.Function;

public final class ImmutableSets {
    private ImmutableSets() {
    }

    @SafeVarargs
    public static <I, R> Set<R> transformed(Function<I, R> transformer, I... elements) {
        Object[] array = new Object[elements.length];

        for (int i = 0; i < elements.length; i++) {
            array[i] = transformer.apply(elements[i]);
        }

        //noinspection unchecked
        return (Set<R>) Set.of(array);
    }
}
