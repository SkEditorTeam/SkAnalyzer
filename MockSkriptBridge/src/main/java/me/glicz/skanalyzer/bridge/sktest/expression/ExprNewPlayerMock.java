package me.glicz.skanalyzer.bridge.sktest.expression;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.glicz.skanalyzer.bridge.sktest.bukkit.event.TestEvent;
import me.glicz.skanalyzer.mockbukkit.AnalyzerPlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprNewPlayerMock extends SimpleExpression<PlayerMock> {
    static {
        Skript.registerExpression(ExprNewPlayerMock.class, PlayerMock.class, ExpressionType.SIMPLE, "new player mock");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    protected PlayerMock @NotNull [] get(@NotNull Event event) {
        if (event instanceof TestEvent e) {
            AnalyzerPlayer player = e.getServer().addPlayer();

            e.registerPlayerMock(player);

            return new PlayerMock[]{player};
        }
        return new PlayerMock[]{null};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends PlayerMock> getReturnType() {
        return PlayerMock.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "new player mock";
    }
}
