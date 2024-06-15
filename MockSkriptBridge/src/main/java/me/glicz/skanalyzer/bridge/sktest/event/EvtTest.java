package me.glicz.skanalyzer.bridge.sktest.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.glicz.skanalyzer.bridge.sktest.SkTestLogger;
import me.glicz.skanalyzer.bridge.sktest.bukkit.event.TestEvent;
import me.glicz.skanalyzer.mockbukkit.AnalyzerServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public class EvtTest extends SkriptEvent implements SkTestLogger {
    static {
        Skript.registerEvent("*Test", EvtTest.class, TestEvent.class, "test %-string%");
    }

    private String testName = null;

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        this.testName = (String) args[0].getSingle();
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
        testLog("Running test");

        TestEvent event = new TestEvent((AnalyzerServer) Bukkit.getServer(), testName);

        boolean result = trigger.forceExecute(event);
        if (result) {
            testLog("Test succeeded");
        } else {
            testLog("Test failed");
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
        return "test " + testName;
    }
}
