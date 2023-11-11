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
import me.glicz.skanalyzer.SkAnalyzer;
import me.glicz.skanalyzer.structure.StructureType;
import me.glicz.skanalyzer.structure.data.EventData;
import me.glicz.skanalyzer.structure.data.FunctionData;
import me.glicz.skanalyzer.structure.data.StructureData;
import org.bukkit.Bukkit;
import org.skriptlang.skript.lang.script.Script;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockSkriptBridgeImpl extends MockSkriptBridge {
    private static final Field scriptCommandField, exprField, skriptEventInfoField, structureField;

    static {
        Field tempScriptCommandField = null;
        try {
            tempScriptCommandField = StructCommand.class.getDeclaredField("scriptCommand");
            tempScriptCommandField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        scriptCommandField = tempScriptCommandField;

        Field tempExprField = null;
        try {
            tempExprField = SkriptEvent.class.getDeclaredField("expr");
            tempExprField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        exprField = tempExprField;

        Field tempSkriptEventInfo = null;
        try {
            tempSkriptEventInfo = SkriptEvent.class.getDeclaredField("skriptEventInfo");
            tempSkriptEventInfo.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        skriptEventInfoField = tempSkriptEventInfo;

        Field tempStructureField = null;
        try {
            tempStructureField = StructFunction.class.getDeclaredField("signature");
            tempStructureField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        structureField = tempStructureField;
    }

    public MockSkriptBridgeImpl() {
        if (!Bukkit.getName().equals("SkAnalyzer"))
            throw new RuntimeException("MockSkriptBridge only supports SkAnalyzer.");
    }

    @Override
    public void parseArgs(List<String> args) {
        if (args.contains("--forceVaultHook")) {
            try {
                String basePackage = VaultHook.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage + ".economy");
                Skript.getAddonInstance().loadClasses(basePackage + ".chat");
                Skript.getAddonInstance().loadClasses(basePackage + ".permission");
                SkAnalyzer.get().getLogger().info("Force loaded Vault hook");
            } catch (IOException e) {
                SkAnalyzer.get().getLogger().error("Something went wrong while trying to force load Vault hook", e);
            }
        }
        if (args.contains("--forceRegionsHook")) {
            try {
                String basePackage = RegionsPlugin.class.getPackage().getName();
                Skript.getAddonInstance().loadClasses(basePackage);
                SkAnalyzer.get().getLogger().info("Force loaded regions hook");
            } catch (IOException e) {
                SkAnalyzer.get().getLogger().error("Something went wrong while trying to force load regions hook", e);
            }
        }
    }

    @Override
    public void parseScript(String path) {
        File file = new File(path);
        if (!file.exists() || !file.getName().endsWith(".sk")) {
            SkAnalyzer.get().getLogger().error("Invalid file path");
            return;
        }
        AnalyzerCommandSender sender = new AnalyzerCommandSender();
        RedirectingLogHandler logHandler = new RedirectingLogHandler(sender, null).start();
        ScriptLoader.loadScripts(file, logHandler, false).whenComplete((info, throwable) -> {
            if (throwable != null) {
                SkAnalyzer.get().getLogger().error("Something went wrong while trying to parse '%s'".formatted(path), throwable);
                return;
            }
            Map<StructureType, List<StructureData>> structures = new HashMap<>();
            Script script = ScriptLoader.getScript(file);
            if (script != null) {
                script.getStructures().forEach(structure -> {
                    if (structure instanceof StructCommand command) {
                        ScriptCommand scriptCommand = getScriptCommand(command);
                        if (scriptCommand == null) return;
                        structures.putIfAbsent(StructureType.COMMAND, new ArrayList<>());
                        structures.get(StructureType.COMMAND).add(new StructureData(
                                command.getEntryContainer().getSource().getLine(),
                                scriptCommand.getName()
                        ));
                    } else if (structure instanceof SkriptEvent event) {
                        SkriptEventInfo<?> eventInfo = getEventInfo(event);
                        if (eventInfo == null) return;
                        structures.putIfAbsent(StructureType.EVENT, new ArrayList<>());
                        structures.get(StructureType.EVENT).add(new EventData(
                                event.getEntryContainer().getSource().getLine(),
                                getEventExpression(event),
                                eventInfo.getId(),
                                event.getEventPriority()
                        ));
                    } else if (structure instanceof StructFunction function) {
                        Signature<?> signature = getFunctionSignature(function);
                        if (signature == null) return;
                        structures.putIfAbsent(StructureType.FUNCTION, new ArrayList<>());
                        structures.get(StructureType.FUNCTION).add(new FunctionData(
                                function.getEntryContainer().getSource().getLine(),
                                signature.getName(),
                                signature.isLocal()
                        ));
                    }
                });
                ScriptLoader.unloadScript(script);
            }
            sender.finish(Map.of(getCanonicalPath(file).replace('\\', '/'), structures));
            logHandler.close();
        });
    }

    private String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ScriptCommand getScriptCommand(StructCommand command) {
        try {
            return (ScriptCommand) scriptCommandField.get(command);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private String getEventExpression(SkriptEvent event) {
        try {
            return (String) exprField.get(event);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private SkriptEventInfo<?> getEventInfo(SkriptEvent event) {
        try {
            return (SkriptEventInfo<?>) skriptEventInfoField.get(event);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private Signature<?> getFunctionSignature(StructFunction function) {
        try {
            return (Signature<?>) structureField.get(function);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}
