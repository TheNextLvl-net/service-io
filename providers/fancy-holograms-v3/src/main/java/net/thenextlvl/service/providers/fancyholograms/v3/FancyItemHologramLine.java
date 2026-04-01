package net.thenextlvl.service.providers.fancyholograms.v3;

import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class FancyItemHologramLine extends FancyHologramLine<ItemHologramData> implements ItemHologramLine {
    public FancyItemHologramLine(final FancyHologram hologram, final ItemHologramData data) {
        super(hologram, data);
    }

    @Override
    public LineType getType() {
        return LineType.ITEM;
    }

    @Override
    public ItemStack getItemStack() {
        return data.getItemStack();
    }

    @Override
    public boolean setItemStack(@Nullable final ItemStack item) {
        if (Objects.equals(data.getItemStack(), item)) return false;
        data.setItemStack(item);
        return true;
    }

    @Override
    public boolean isPlayerHead() {
        return false;
    }

    @Override
    public boolean setPlayerHead(final boolean playerHead) {
        return false;
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return ItemDisplay.ItemDisplayTransform.FIXED;
    }

    @Override
    public boolean setItemDisplayTransform(final ItemDisplay.ItemDisplayTransform display) {
        return false;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return ItemDisplay.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ITEM_DISPLAY;
    }
}
