package me.glicz.skanalyzer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.base.Suppliers;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.net.URL;
import java.util.function.Supplier;

@Getter
public enum LoggerType {
    NORMAL,
    PLAIN,
    DISABLED(() -> NOPLogger.NOP_LOGGER);

    private final Supplier<Logger> loggerFactory;

    LoggerType() {
        this(Suppliers.ofInstance(LoggerFactory.getLogger("SkAnalyzer")));
    }

    LoggerType(Supplier<Logger> loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public void loadConfiguration() {
        URL config = getClass().getResource("/" + name().toLowerCase() + "-logback.xml");
        if (config == null) return;

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);

        try {
            configurator.doConfigure(config);
        } catch (JoranException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return loggerFactory.get();
    }
}
