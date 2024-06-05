package me.glicz.skanalyzer.bridge.sktest.bukkit.event;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.glicz.skanalyzer.mockbukkit.AnalyzerServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class TestEvent extends Event {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Set<PlayerMock> playerMocks = new HashSet<>();
    private final Set<WorldMock> worldMocks = new HashSet<>();
    @Getter
    private final AnalyzerServer server;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public void registerPlayerMock(PlayerMock playerMock) {
        playerMocks.add(playerMock);
        server.addPlayer(playerMock);
    }

    public void registerWorldMock(WorldMock worldMock) {
        worldMocks.add(worldMock);
        server.addWorld(worldMock);
    }

    public void postTest() {
        playerMocks.removeIf(playerMock -> {
            playerMock.disconnect();
            return true;
        });

        worldMocks.removeIf(worldMock -> {
            server.removeWorld(worldMock);
            return true;
        });
    }
}
