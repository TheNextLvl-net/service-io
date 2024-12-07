package net.thenextlvl.service.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface HologramLine {
    Optional<BlockData> getBlockData();

    Optional<Component> getText();

    Optional<ItemStack> getItem();

    void remove();

    void setBlockData(BlockData blockData);

    void setItem(ItemStack item);

    void setText(Component text);
}
