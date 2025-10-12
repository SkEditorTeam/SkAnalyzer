package me.glicz.skanalyzer.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record Config(
        ForcedHooks forcedHooks
) {
    @ConfigSerializable
    public record ForcedHooks(
            boolean vault,
            boolean regions
    ) {
    }
}
