package me.glicz.skanalyzer.server.potion;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@SuppressWarnings("removal")
public class AnalyzerPotionBrewer implements PotionBrewer {
    @Override
    public void addPotionMix(@NotNull PotionMix potionMix) {

    }

    @Override
    public void removePotionMix(@NotNull NamespacedKey key) {

    }

    @Override
    public void resetPotionMixes() {

    }

    @Override
    public @NotNull Collection<PotionEffect> getEffects(@NotNull PotionType type, boolean upgraded, boolean extended) {
        return type.getPotionEffects();
    }
}
