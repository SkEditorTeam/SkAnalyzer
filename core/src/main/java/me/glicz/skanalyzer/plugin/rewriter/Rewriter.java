package me.glicz.skanalyzer.plugin.rewriter;

import java.util.function.Predicate;

public interface Rewriter<T> extends Predicate<T> {
    T rewrite(T t);
}
