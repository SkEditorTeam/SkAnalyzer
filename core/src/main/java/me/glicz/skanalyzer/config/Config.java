package me.glicz.skanalyzer.config;

import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static me.glicz.skanalyzer.config.Config.ForcedHooks.defaultForcedHooks;

@ConfigSerializable
public record Config(
        ForcedHooks forcedHooks
) {
    @Contract(value = " -> new", pure = true)
    public static Config defaultConfig() {
        return new Config(defaultForcedHooks());
    }

    @ConfigSerializable
    public record ForcedHooks(
            boolean vault,
            boolean regions
    ) {
        @Contract(value = " -> new", pure = true)
        public static ForcedHooks defaultForcedHooks() {
            return new ForcedHooks(false, false);
        }
    }
}
