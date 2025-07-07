package net.minecraft.nbt;

/**
 * A dummy class to make NBT in SkBee work with SkAnalyzer.
 * <p>
 * The only thing this class does is provide {@linkplain TagParser#parseCompoundFully(String)} method,
 * to make SkBee actually initialize NBT API and successfully load.
 */
public final class TagParser {
    private TagParser() {
    }

    public static void parseCompoundFully(String tag) {
        // no need to do anything here
    }
}
