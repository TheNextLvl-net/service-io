package net.thenextlvl.service.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public enum LineType {
    BLOCK(BlockData.class),
    ENTITY(Entity.class),
    ITEM(ItemStack.class),
    TEXT(Component.class);

    private final Class<?> type;

    LineType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
