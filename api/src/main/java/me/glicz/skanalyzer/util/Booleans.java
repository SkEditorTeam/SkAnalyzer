package me.glicz.skanalyzer.util;

public final class Booleans {
    private Booleans() {
    }

    public static boolean and(boolean... array) {
        for (boolean element : array) {
            if (!element) {
                return false;
            }
        }
        return true;
    }

    public static boolean or(boolean... array) {
        for (boolean element : array) {
            if (element) {
                return true;
            }
        }
        return false;
    }
}
