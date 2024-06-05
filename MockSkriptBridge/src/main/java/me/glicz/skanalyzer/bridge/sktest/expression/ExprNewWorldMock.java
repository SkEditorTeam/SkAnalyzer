package me.glicz.skanalyzer.bridge.sktest.expression;

import be.seeseemelk.mockbukkit.WorldMock;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.glicz.skanalyzer.bridge.sktest.bukkit.event.TestEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprNewWorldMock extends SimpleExpression<WorldMock> {
    static {
        Skript.registerExpression(ExprNewWorldMock.class, WorldMock.class, ExpressionType.SIMPLE, "new world mock named %-string%");
    }

    private Expression<String> name;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        this.name = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected WorldMock @NotNull [] get(@NotNull Event event) {
        if (event instanceof TestEvent e) {
            WorldMock worldMock = e.getServer().addSimpleWorld(name.getSingle(event));

            e.registerWorldMock(worldMock);

            return new WorldMock[]{worldMock};
        }
        return new WorldMock[]{null};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends WorldMock> getReturnType() {
        return WorldMock.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "new player mock";
    }
}
