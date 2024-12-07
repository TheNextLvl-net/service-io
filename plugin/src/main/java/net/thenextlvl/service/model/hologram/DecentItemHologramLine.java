package net.thenextlvl.service.model.hologram;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import net.thenextlvl.service.api.hologram.LineType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DecentItemHologramLine extends DecentHologramLine<ItemStack> {
    public DecentItemHologramLine(HologramLine line) {
        super(line);
    }

    @Override
    public LineType getType() {
        return LineType.ITEM;
    }

    @Override
    public ItemStack getContent() {
        return line.getItem().parse(null);
    }

    @Override
    public void setContent(ItemStack itemStack) {
        line.setContent("#ICON:" + HologramItem.fromItemStack(itemStack).getContent());
    }
}
