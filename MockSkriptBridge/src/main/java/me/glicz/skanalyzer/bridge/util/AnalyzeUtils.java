package me.glicz.skanalyzer.bridge.util;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.structures.StructCommand;
import ch.njol.skript.structures.StructEvent;
import ch.njol.skript.structures.StructFunction;
import ch.njol.skript.structures.StructOptions;
import me.glicz.skanalyzer.result.structure.ScriptStructure;
import me.glicz.skanalyzer.result.structure.data.CommandData;
import me.glicz.skanalyzer.result.structure.data.EventData;
import me.glicz.skanalyzer.result.structure.data.FunctionData;
import org.bukkit.event.EventPriority;
import org.skriptlang.skript.lang.script.Script;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static me.glicz.skanalyzer.bridge.util.ObjectUtils.transformValue;
import static me.glicz.skanalyzer.bridge.util.ObjectUtils.nonNull;
import static me.glicz.skanalyzer.bridge.util.StringUtils.emptyToNull;

public final class AnalyzeUtils {
    private AnalyzeUtils() {
    }

    public static ScriptStructure toScriptStructure(Script script) {
        List<CommandData> commands = new ArrayList<>();
        List<EventData> events = new ArrayList<>();
        List<FunctionData> functions = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        script.getStructures().forEach(structure -> {
            switch (structure) {
                case StructCommand command -> {
                    ScriptCommand scriptCommand = command.scriptCommand;
                    if (scriptCommand == null) return;

                    commands.add(toCommandData(command, scriptCommand));
                }
                case StructEvent event -> {
                    SkriptEventInfo<?> eventInfo = event.getSkriptEvent().skriptEventInfo;

                    events.add(toEventData(event.getSkriptEvent(), eventInfo));
                }
                case StructFunction function -> {
                    Signature<?> signature = function.signature;
                    if (signature == null) return;

                    functions.add(toFunctionData(function, signature));
                }
                default -> {
                }
            }
        });

        StructOptions.OptionsData optionsData = script.getData(StructOptions.OptionsData.class);
        if (optionsData != null) {
            options.putAll(optionsData.getOptions());
        }

        return new ScriptStructure(commands, events, functions, options, script.addons);
    }

    public static CommandData toCommandData(StructCommand command, ScriptCommand scriptCommand) {
        int line = command.getEntryContainer().getSource().getLine();
        String name = scriptCommand.getName();
        List<String> aliases = scriptCommand.getAliases();

        String permission = emptyToNull(scriptCommand.permission);
        String description = emptyToNull(scriptCommand.description);
        String prefix = scriptCommand.getPrefix();
        String usage = emptyToNull(scriptCommand.usage.getUsage());

        List<String> arguments = scriptCommand.getArguments().stream()
                .map(argument -> argument.type.getCodeName())
                .toList();

        return new CommandData(line, name, aliases, permission, description, prefix, usage, arguments);
    }

    public static EventData toEventData(SkriptEvent event, SkriptEventInfo<?> eventInfo) {
        int line = event.getEntryContainer().getSource().getLine();
        String expression = event.expr;
        String id = nonNull(eventInfo.getDocumentationID(), eventInfo.getId());
        EventPriority priority = event.getEventPriority();

        return new EventData(line, expression, id, priority);
    }

    public static FunctionData toFunctionData(StructFunction function, Signature<?> signature) {
        int line = function.getEntryContainer().getSource().getLine();
        String name = signature.getName();
        boolean local = signature.isLocal();

        Map<String, String> parameters = stream(signature.getParameters()).collect(toMap(
                Parameter::getName,
                parameter -> parameter.getType().getCodeName(),
                (left, right) -> left,
                LinkedHashMap::new
        ));

        String returnType = transformValue(signature.getReturnType(), ClassInfo::getCodeName);

        return new FunctionData(line, name, local, parameters, returnType);
    }
}
