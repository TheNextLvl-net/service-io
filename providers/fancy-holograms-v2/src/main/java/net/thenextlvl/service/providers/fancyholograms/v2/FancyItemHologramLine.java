package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.ItemHologramData;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FancyItemHologramLine extends FancyHologramLine<ItemHologramData, ItemStack> {
    public FancyItemHologramLine(final ItemHologramData data) {
        super(data);
    }

    @Override
    public ItemStack getContent() {
        return data.getItemStack();
    }

    @Override
    public void setContent(final ItemStack content) {
        data.setItemStack(content);
    }
}
