package me.glicz.skanalyzer.server.scoreboard;

import me.glicz.skanalyzer.server.AnalyzerServer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;

import java.util.Map;

/**
 * A dummy class to make scoreboard objectives in SkBee work with SkAnalyzer.
 * <p>
 * The only thing this class does is provide {@linkplain CraftCriteria#DEFAULTS} field,
 * to make SkBee actually initialize scoreboard objectives and successfully load.
 */
public final class CraftCriteria {
    private static final Map<String, Criteria> DEFAULTS = Map.copyOf(((AnalyzerServer) Bukkit.getServer()).getScoreboardCriteria());

    private CraftCriteria() {
    }
}
