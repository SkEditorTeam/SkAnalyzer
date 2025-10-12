package me.glicz.skanalyzer.server.potion;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Collection;

@SuppressWarnings("removal")
public class AnalyzerPotionBrewer implements PotionBrewer {
    @Override
    public void addPotionMix(PotionMix potionMix) {

    }

    @Override
    public void removePotionMix(NamespacedKey key) {

    }

    @Override
    public void resetPotionMixes() {

    }

    @Override
    public Collection<PotionEffect> getEffects(PotionType type, boolean upgraded, boolean extended) {
        return type.getPotionEffects();
    }
}
