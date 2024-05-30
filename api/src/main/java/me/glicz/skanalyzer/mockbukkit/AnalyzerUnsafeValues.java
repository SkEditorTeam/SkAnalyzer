package me.glicz.skanalyzer.mockbukkit;

import be.seeseemelk.mockbukkit.MockUnsafeValues;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class AnalyzerUnsafeValues extends MockUnsafeValues {
    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return stack;
    }
}
