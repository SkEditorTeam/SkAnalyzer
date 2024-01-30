package me.glicz.skanalyzer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.Level;

@Getter
@AllArgsConstructor
public enum LoggerType {
    DISABLED(null, Level.OFF),
    NORMAL("SkAnalyzer", Level.ALL),
    PLAIN("PlainLogger", Level.ALL);

    private final String loggerName;
    private final Level loggerLevel;
}
