package me.glicz.skanalyzer.server;

import org.bukkit.inventory.ItemStack;
import org.mockbukkit.mockbukkit.util.UnsafeValuesMock;

@SuppressWarnings("deprecation")
public class AnalyzerUnsafeValues extends UnsafeValuesMock {
    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return stack;
    }
}
