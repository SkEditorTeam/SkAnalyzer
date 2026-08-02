package me.glicz.skanalyzer.server.inventory;

import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.mockbukkit.mockbukkit.inventory.ItemFactoryMock;

public class AnalyzerItemFactory extends ItemFactoryMock {
    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        Key itemTypeKey = NamespacedKey.fromString(StringUtils.substringBefore(input, "["));
        ItemType itemType = itemTypeKey != null ? Registry.ITEM.get(itemTypeKey) : null;

        if (itemType == null) {
            throw new IllegalArgumentException();
        }

        return itemType.createItemStack();
    }
}
