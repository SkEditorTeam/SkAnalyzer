package me.glicz.skanalyzer.util.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Optional;

public class EffectiveLoggerConverter extends ClassicConverter {
    private int maxLength = Integer.MAX_VALUE;

    @Override
    public void start() {
        maxLength = Optional.ofNullable(getFirstOption())
                .map(Integer::parseInt)
                .orElse(Integer.MAX_VALUE);
    }

    @Override
    public String convert(ILoggingEvent event) {
        String loggerName = event.getLoggerName();

        if (loggerName == null || loggerName.isEmpty()) {
            return "";
        }

        if (loggerName.length() > maxLength) {
            loggerName = loggerName.substring(loggerName.length() - maxLength);
        }

        return "[" + loggerName + "] ";
    }
}
