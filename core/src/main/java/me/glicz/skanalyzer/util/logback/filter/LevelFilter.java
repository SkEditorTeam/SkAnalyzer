package me.glicz.skanalyzer.util.logback.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.function.BiPredicate;

import static me.glicz.skanalyzer.util.function.BiPredicates.reversed;

public class LevelFilter extends Filter<ILoggingEvent> {
    private @MonotonicNonNull Level level;
    private @MonotonicNonNull Matcher matcher;

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted() || matcher.predicate.test(event.getLevel(), level)) {
            return FilterReply.NEUTRAL;
        }

        return FilterReply.DENY;
    }

    @Override
    public void start() {
        if (level != null && matcher != null) {
            super.start();
        }
    }

    public enum Matcher {
        HIGHER_OR_EQUAL(Level::isGreaterOrEqual),
        LOWER_OR_EQUAL(reversed(Level::isGreaterOrEqual)),
        ;

        private final BiPredicate<Level, Level> predicate;

        Matcher(BiPredicate<Level, Level> predicate) {
            this.predicate = predicate;
        }
    }
}
