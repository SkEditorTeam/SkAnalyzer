package me.glicz.skanalyzer.bridge;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.RegionsPlugin;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.structures.StructCommand;
import ch.njol.skript.structures.StructEvent;
import ch.njol.skript.structures.StructFunction;
import ch.njol.skript.structures.StructOptions;
import me.glicz.skanalyzer.AnalyzerFlag;
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.bridge.log.CachingLogHandler;
import me.glicz.skanalyzer.bridge.util.FilesUtil;
import me.glicz.skanalyzer.result.ScriptAnalyzeResult;
import me.glicz.skanalyzer.result.ScriptAnalyzeResults;
import me.glicz.skanalyzer.structure.ScriptStructure;
import me.glicz.skanalyzer.structure.data.CommandData;
import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import org.apache.commons.lang3.StringUtils;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockSkriptBridgeImpl extends MockSkriptBridge {
    private Executor mainThreadExecutor;

    public MockSkriptBridgeImpl(SkAnalyzer skAnalyzer) {
        super(skAnalyzer);
    }

    @Override
    public void onLoad() {
        parseFlags();
    }

    @Override
    public void onEnable() {
        mainThreadExecutor = getServer().getScheduler().getMainThreadExecutor(this);
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
    public CompletableFuture<ScriptAnalyzeResults> parseScript(String path, boolean load) {
        File file = new File(path);
        if (!file.exists() || (!file.getName().endsWith(".sk") && !(file.isDirectory() && load))) {
            skAnalyzer.getLogger().error("Invalid file path");
            return CompletableFuture.failedFuture(new InvalidPathException(path, "Provided file doesn't end with '.sk'"));
        }

        Set<File> files = FilesUtil.listScripts(file);
        return CompletableFuture.supplyAsync(
                () -> {
                    CachingLogHandler logHandler = new CachingLogHandler().start();
                    return ScriptLoader.loadScripts(files, logHandler)
                            .handle((info, throwable) -> {
                                if (throwable != null) {
                                    skAnalyzer.getLogger().error("Something went wrong while trying to parse '%s'".formatted(path), throwable);
                                    throw new RuntimeException(throwable);
                                }
                                return info;
                            })
                            .thenApply(info -> {
                                ScriptAnalyzeResults results = new ScriptAnalyzeResults(buildAnalyzeResults(files, logHandler));
                                if (!load) {
                                    unloadScript(path);
                                }
                                return results;
                            })
                            .join();
                },
                mainThreadExecutor
        );
    }

    @Override
    public boolean unloadScript(String path) {
        File file = new File(path);
        if (!file.exists() || !file.getName().endsWith(".sk")) {
            skAnalyzer.getLogger().error("Invalid file path");
            return false;
        }

        Script script = ScriptLoader.getScript(file);
        if (script != null) {
            ScriptLoader.unloadScript(script);
            return true;
        }
        return false;
    }

    @Override
    public void unloadAllScripts() {
        ScriptLoader.unloadScripts(ScriptLoader.getLoadedScripts());
    }

    private Map<File, ScriptAnalyzeResult> buildAnalyzeResults(Set<File> files, CachingLogHandler logHandler) {
        return files.stream().collect(Collectors.toMap(
                Function.identity(),
                file -> new ScriptAnalyzeResult(
                        file,
                        logHandler.scriptErrors(file),
                        handleParsedScript(file)
                )
        ));
    }

    private ScriptStructure handleParsedScript(File file) {
        List<CommandData> commandDataList = new ArrayList<>();
        List<EventData> eventDataList = new ArrayList<>();
        List<FunctionData> functionDataList = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        Script script = ScriptLoader.getScript(file);
        if (script != null) {
            script.getStructures().forEach(structure -> {
                if (structure instanceof StructCommand command) {
                    ScriptCommand scriptCommand = command.scriptCommand;
                    if (scriptCommand == null) return;
                    commandDataList.add(handleCommand(command, scriptCommand));
                } else if (structure instanceof StructEvent event) {
                    SkriptEventInfo<?> eventInfo = event.getSkriptEvent().skriptEventInfo;
                    eventDataList.add(handleEvent(event.getSkriptEvent(), eventInfo));
                } else if (structure instanceof StructFunction function) {
                    Signature<?> signature = function.signature;
                    if (signature == null) return;
                    functionDataList.add(handleFunction(function, signature));
                }
            });

            StructOptions.OptionsData optionsData = script.getData(StructOptions.OptionsData.class);
            if (optionsData != null) {
                options.putAll(optionsData.getOptions());
            }

            ScriptLoader.unloadScript(script);
        }

        return new ScriptStructure(commandDataList, eventDataList, functionDataList, options);
    }

    private CommandData handleCommand(StructCommand command, ScriptCommand scriptCommand) {
        return new CommandData(
                command.getEntryContainer().getSource().getLine(),
                scriptCommand.getName(),
                scriptCommand.getAliases(),
                StringUtils.defaultIfEmpty(scriptCommand.permission, null),
                StringUtils.defaultIfEmpty(scriptCommand.description, null),
                scriptCommand.getPrefix(),
                StringUtils.defaultIfEmpty(scriptCommand.usage, null),
                scriptCommand.getArguments().stream()
                        .map(argument -> argument.type.getCodeName())
                        .toList()
        );
    }

    private EventData handleEvent(SkriptEvent event, SkriptEventInfo<?> eventInfo) {
        return new EventData(
                event.getEntryContainer().getSource().getLine(),
                event.expr,
                Objects.requireNonNullElse(eventInfo.getDocumentationID(), eventInfo.getId()),
                event.getEventPriority()
        );
    }

    private FunctionData handleFunction(StructFunction function, Signature<?> signature) {
        String returnType = null;
        if (signature.getReturnType() != null) {
            returnType = signature.getReturnType().getCodeName();
        }

        return new FunctionData(
                function.getEntryContainer().getSource().getLine(),
                signature.getName(),
                signature.isLocal(),
                Arrays.stream(signature.getParameters())
                        .map(parameter -> Map.entry(parameter.getName(), parameter.getType().getCodeName()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (s, s2) -> s,
                                LinkedHashMap::new
                        )),
                returnType
        );
    }
}
