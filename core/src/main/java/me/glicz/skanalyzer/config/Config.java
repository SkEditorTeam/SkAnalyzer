package me.glicz.skanalyzer.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class Config {
    public ForcedHooks forcedHooks = new ForcedHooks();

    @ConfigSerializable
    public static final class ForcedHooks {
        public boolean vault;
        public boolean regions;
    }
}
