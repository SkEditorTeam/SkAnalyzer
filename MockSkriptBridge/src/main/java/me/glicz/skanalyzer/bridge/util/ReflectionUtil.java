package me.glicz.skanalyzer.bridge.util;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.command.Argument;
import ch.njol.skript.command.ScriptCommand;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.structures.StructCommand;
import ch.njol.skript.structures.StructFunction;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtil {
    private static final Field scriptCommandField, commandPermissionField, commandDescriptionField,
            commandUsageField, argumentTypeField, exprField, skriptEventInfoField, structureField;

    static {
        Field tempScriptCommandField = null;
        try {
            tempScriptCommandField = StructCommand.class.getDeclaredField("scriptCommand");
            tempScriptCommandField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        scriptCommandField = tempScriptCommandField;

        Field tempCommandPermissionField = null;
        try {
            tempCommandPermissionField = ScriptCommand.class.getDeclaredField("permission");
            tempCommandPermissionField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        commandPermissionField = tempCommandPermissionField;

        Field tempCommandDescriptionField = null;
        try {
            tempCommandDescriptionField = ScriptCommand.class.getDeclaredField("description");
            tempCommandDescriptionField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        commandDescriptionField = tempCommandDescriptionField;

        Field tempCommandUsageField = null;
        try {
            tempCommandUsageField = ScriptCommand.class.getDeclaredField("usage");
            tempCommandUsageField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        commandUsageField = tempCommandUsageField;


        Field tempArgumentTypeField = null;
        try {
            tempArgumentTypeField = Argument.class.getDeclaredField("type");
            tempArgumentTypeField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        argumentTypeField = tempArgumentTypeField;

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

    public static ScriptCommand getScriptCommand(StructCommand command) {
        try {
            return (ScriptCommand) scriptCommandField.get(command);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static String getCommandPermission(ScriptCommand command) {
        try {
            return (String) commandPermissionField.get(command);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static String getCommandDescription(ScriptCommand command) {
        try {
            return (String) commandDescriptionField.get(command);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static String getCommandUsage(ScriptCommand command) {
        try {
            return (String) commandUsageField.get(command);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static ClassInfo<?> getArgumentType(Argument<?> argument) {
        try {
            return (ClassInfo<?>) argumentTypeField.get(argument);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static String getEventExpression(SkriptEvent event) {
        try {
            return (String) exprField.get(event);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static SkriptEventInfo<?> getEventInfo(SkriptEvent event) {
        try {
            return (SkriptEventInfo<?>) skriptEventInfoField.get(event);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public static Signature<?> getFunctionSignature(StructFunction function) {
        try {
            return (Signature<?>) structureField.get(function);
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}
