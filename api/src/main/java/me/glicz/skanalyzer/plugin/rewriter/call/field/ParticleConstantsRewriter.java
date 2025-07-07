package me.glicz.skanalyzer.plugin.rewriter.call.field;

import me.glicz.skanalyzer.plugin.rewriter.Rewriter;
import me.glicz.skanalyzer.util.Booleans;
import org.objectweb.asm.Opcodes;

public final class ParticleConstantsRewriter implements Rewriter<FieldCall> {
    public static final ParticleConstantsRewriter INSTANCE = new ParticleConstantsRewriter();

    private ParticleConstantsRewriter() {
    }

    public static String rewrite(String name) {
        return switch (name) {
            case "EXPLOSION_NORMAL" -> "POOF";
            case "EXPLOSION_LARGE" -> "EXPLOSION";
            case "EXPLOSION_HUGE" -> "EXPLOSION_EMITTER";
            case "FIREWORKS_SPARK" -> "FIREWORK";
            case "WATER_BUBBLE" -> "BUBBLE";
            case "WATER_SPLASH" -> "SPLASH";
            case "WATER_WAKE" -> "FISHING";
            case "SUSPENDED", "SUSPENDED_DEPTH" -> "UNDERWATER";
            case "CRIT_MAGIC" -> "ENCHANTED_HIT";
            case "SMOKE_NORMAL" -> "SMOKE";
            case "SMOKE_LARGE" -> "LARGE_SMOKE";
            case "SPELL" -> "EFFECT";
            case "SPELL_INSTANT" -> "INSTANT_EFFECT";
            case "SPELL_MOB" -> "ENTITY_EFFECT";
            case "SPELL_WITCH" -> "WITCH";
            case "DRIP_WATER" -> "DRIPPING_WATER";
            case "DRIP_LAVA" -> "DRIPPING_LAVA";
            case "VILLAGER_ANGRY" -> "ANGRY_VILLAGER";
            case "VILLAGER_HAPPY" -> "HAPPY_VILLAGER";
            case "TOWN_AURA" -> "MYCELIUM";
            case "ENCHANTMENT_TABLE" -> "ENCHANT";
            case "REDSTONE" -> "DUST";
            case "SNOWBALL", "SNOW_SHOVEL" -> "ITEM_SNOWBALL";
            case "SLIME" -> "ITEM_SLIME";
            case "ITEM_CRACK" -> "ITEM";
            case "BLOCK_CRACK", "BLOCK_DUST" -> "BLOCK";
            case "WATER_DROP" -> "RAIN";
            case "MOB_APPEARANCE" -> "ELDER_GUARDIAN";
            case "TOTEM" -> "TOTEM_OF_UNDYING";
            case "GUST_EMITTER" -> "GUST_EMITTER_LARGE";
            default -> name;
        };
    }

    @Override
    public FieldCall rewrite(FieldCall call) {
        String name = rewrite(call.name());
        if (name.equals(call.name())) {
            return call;
        }

        return new FieldCall(call.opcode(), call.owner(), name, call.descriptor());
    }

    @Override
    public boolean test(FieldCall call) {
        return Booleans.and(
                call.opcode() == Opcodes.GETSTATIC,
                call.owner().equals("org/bukkit/Particle")
        );
    }
}
