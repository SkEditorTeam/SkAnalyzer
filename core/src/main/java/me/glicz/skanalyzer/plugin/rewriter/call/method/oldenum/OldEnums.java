package me.glicz.skanalyzer.plugin.rewriter.call.method.oldenum;

import me.glicz.skanalyzer.util.ImmutableSets;
import org.bukkit.entity.Villager;
import org.objectweb.asm.Type;

import java.util.Set;

final class OldEnums {
    static final Set<String> OLD_ENUMS = ImmutableSets.transformed(
            Type::getInternalName,
            Villager.Profession.class
    );

    private OldEnums() {
    }
}
