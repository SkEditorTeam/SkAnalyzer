package me.glicz.skanalyzer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.net.URL;

@Getter
@AllArgsConstructor
public enum LoggerType {
    NORMAL("/"),
    PLAIN("/plain-logback.xml"),
    DISABLED("/disabled-logback.xml");

    private final String configFile;

    public void loadConfiguration() {
        URL config = getClass().getResource(configFile);
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
}
