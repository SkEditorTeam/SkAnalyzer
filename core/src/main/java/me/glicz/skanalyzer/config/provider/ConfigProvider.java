package me.glicz.skanalyzer.config.provider;

import me.glicz.skanalyzer.config.Config;

import java.io.IOException;

@FunctionalInterface
public interface ConfigProvider {
    Config loadConfig() throws IOException;
}
