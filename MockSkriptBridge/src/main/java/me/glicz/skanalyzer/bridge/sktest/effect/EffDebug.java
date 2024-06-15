package me.glicz.skanalyzer.bridge.sktest.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.glicz.skanalyzer.bridge.sktest.bukkit.event.TestEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffDebug extends Effect {
    static {
        Skript.registerEffect(EffDebug.class, "debug %-string%");
    }

    private Expression<String> text = null;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        this.text = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        if (event instanceof TestEvent e) {
            e.testLog(text.getSingle(event));
        }
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "debug " + text.toString(event, debug);
    }
}
