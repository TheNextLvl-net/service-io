package net.thenextlvl.service.hologram.fancy.v3.model;

import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FancyItemHologramLine extends FancyHologramLine<ItemHologramData, ItemStack> {
    public FancyItemHologramLine(ItemHologramData data) {
        super(data);
    }

    @Override
    public ItemStack getContent() {
        return data.getItemStack();
    }

    @Override
    public void setContent(ItemStack content) {
        data.setItemStack(content);
    }
}
