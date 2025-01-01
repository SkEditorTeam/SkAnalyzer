package me.glicz.skanalyzer.logback.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Setter;

import java.util.function.BiPredicate;

import static me.glicz.skanalyzer.util.function.BiPredicates.reversed;

@Setter
public class LevelFilter extends Filter<ILoggingEvent> {
    private Level level;
    private Matcher matcher;

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
