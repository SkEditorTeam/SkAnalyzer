package me.glicz.skanalyzer.mockbukkit;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class AnalyzerPotionBrewer implements PotionBrewer {
    @Override
    public @NotNull PotionEffect createEffect(@NotNull PotionEffectType potionEffectType, int i, int i1) {
        return new PotionEffect(potionEffectType, i, i1);
    }

    @Override
    public @NotNull Collection<PotionEffect> getEffectsFromDamage(int i) {
        return List.of();
    }

    @Override
    public @NotNull Collection<PotionEffect> getEffects(@NotNull PotionType potionType, boolean b, boolean b1) {
        return List.of();
    }

    @Override
    public void addPotionMix(@NotNull PotionMix potionMix) {

    }

    @Override
    public void removePotionMix(@NotNull NamespacedKey namespacedKey) {

    }

    @Override
    public void resetPotionMixes() {

    }
}
