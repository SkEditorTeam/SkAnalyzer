package me.glicz.skanalyzer.bridge;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.RegionsPlugin;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.log.RedirectingLogHandler;
import ch.njol.skript.structures.StructCommand;
import ch.njol.skript.structures.StructFunction;
import me.glicz.skanalyzer.AnalyzerFlag;
import me.glicz.skanalyzer.ScriptAnalyzeResult;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.bridge.util.ReflectionUtil;
import me.glicz.skanalyzer.structure.ScriptStructure;
import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import me.glicz.skanalyzer.structure.data.StructureData;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MockSkriptBridgeImpl extends MockSkriptBridge {
    public MockSkriptBridgeImpl(SkAnalyzer skAnalyzer) {
        super(skAnalyzer);
        parseFlags();
    }

    public void parseFlags() {
        if (skAnalyzer.getFlags().contains(AnalyzerFlag.FORCE_VAULT_HOOK)) {
            try {
                String basePackage = VaultHook.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage + ".economy");
                Skript.getAddonInstance().loadClasses(basePackage + ".chat");
                Skript.getAddonInstance().loadClasses(basePackage + ".permission");
                skAnalyzer.getLogger().info("Force loaded Vault hook");
            } catch (IOException e) {
                skAnalyzer.getLogger().error("Something went wrong while trying to force load Vault hook", e);
            }
        }
        if (skAnalyzer.getFlags().contains(AnalyzerFlag.FORCE_REGIONS_HOOK)) {
            try {
                String basePackage = RegionsPlugin.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage);
                skAnalyzer.getLogger().info("Force loaded regions hook");
            } catch (IOException e) {
                skAnalyzer.getLogger().error("Something went wrong while trying to force load regions hook", e);
            }
        }
    }

    @Override
    public CompletableFuture<ScriptAnalyzeResult> parseScript(String path) {
        File file = new File(path);
        if (!file.exists() || !file.getName().endsWith(".sk")) {
            skAnalyzer.getLogger().error("Invalid file path");
            return CompletableFuture.failedFuture(new InvalidPathException(path, "Provided file doesn't end with '.sk'"));
        }
        AnalyzerCommandSender sender = new AnalyzerCommandSender(skAnalyzer);
        RedirectingLogHandler logHandler = new RedirectingLogHandler(sender, null).start();
        return ScriptLoader.loadScripts(file, logHandler, false)
                .handle((info, throwable) -> {
                    if (throwable != null) {
                        skAnalyzer.getLogger().error("Something went wrong while trying to parse '%s'".formatted(path), throwable);
                        return CompletableFuture.failedFuture(new RuntimeException(throwable));
                    }
                    return CompletableFuture.completedFuture(info);
                })
                .thenApply(info -> sender.finish(file, handleParsedScript(file)));
    }

    private ScriptStructure handleParsedScript(File file) {
        List<StructureData> commandDataList = new ArrayList<>();
        List<EventData> eventDataList = new ArrayList<>();
        List<FunctionData> functionDataList = new ArrayList<>();
        Script script = ScriptLoader.getScript(file);
        if (script != null) {
            script.getStructures().forEach(structure -> {
                if (structure instanceof StructCommand command) {
                    ScriptCommand scriptCommand = ReflectionUtil.getScriptCommand(command);
                    if (scriptCommand == null) return;
                    commandDataList.add(new StructureData(
                            command.getEntryContainer().getSource().getLine(),
                            scriptCommand.getName()
                    ));
                } else if (structure instanceof SkriptEvent event) {
                    SkriptEventInfo<?> eventInfo = ReflectionUtil.getEventInfo(event);
                    if (eventInfo == null) return;
                    eventDataList.add(new EventData(
                            event.getEntryContainer().getSource().getLine(),
                            ReflectionUtil.getEventExpression(event),
                            eventInfo.getId(),
                            event.getEventPriority()
                    ));
                } else if (structure instanceof StructFunction function) {
                    Signature<?> signature = ReflectionUtil.getFunctionSignature(function);
                    if (signature == null) return;
                    String returnType = null;
                    if (signature.getReturnType() != null)
                        returnType = signature.getReturnType().getName().getSingular();
                    functionDataList.add(new FunctionData(
                            function.getEntryContainer().getSource().getLine(),
                            signature.getName(),
                            signature.isLocal(),
                            Arrays.stream(signature.getParameters())
                                    .map(parameter -> Map.entry(parameter.getName(), parameter.getType().getName().getSingular()))
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                            returnType
                    ));
                }
            });
            ScriptLoader.unloadScript(script);
        }
        return new ScriptStructure(commandDataList, eventDataList, functionDataList);
    }
}
