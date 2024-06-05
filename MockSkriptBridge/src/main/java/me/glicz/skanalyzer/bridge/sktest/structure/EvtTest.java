package me.glicz.skanalyzer.bridge.sktest.structure;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import me.glicz.skanalyzer.bridge.sktest.bukkit.event.TestEvent;
import me.glicz.skanalyzer.mockbukkit.AnalyzerServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EvtTest extends SkriptEvent {
    static {
        Skript.registerEvent("*Test", EvtTest.class, TestEvent.class, "test %-string%");
    }

    private String name = null;

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        this.name = (String) args[0].getSingle();
        return true;
    }

    @Override
    public boolean preLoad() {
        if (false) { // TODO
            Skript.error("Test structure can be only used in .'sktest' file!");
            return false;
        }
        return super.preLoad();
    }

    @Override
    public boolean postLoad() {
        System.out.println("Running test: " + name);

        TestEvent event = new TestEvent((AnalyzerServer) Bukkit.getServer());

        boolean result = trigger.forceExecute(event);
        if (result) {
            System.out.println("Test succeeded: " + name);
        } else {
            System.out.println("Test failed: " + name);
        }

        event.postTest();

        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEventPrioritySupported() {
        return false;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "test " + name;
    }
}
