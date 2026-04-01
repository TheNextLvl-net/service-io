package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.BlockHologramLine;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FancyBlockHologramLine extends FancyHologramLine<BlockHologramData> implements BlockHologramLine {
    public FancyBlockHologramLine(final FancyHologram hologram, final BlockHologramData data) {
        super(hologram, data);
    }

    @Override
    public LineType getType() {
        return LineType.BLOCK;
    }

    @Override
    public BlockData getBlock() {
        return data.getBlock().createBlockData();
    }

    @Override
    public boolean setBlock(final BlockData block) {
        if (data.getBlock() == block.getMaterial()) return false;
        data.setBlock(block.getMaterial());
        return true;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return BlockDisplay.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BLOCK_DISPLAY;
    }
}
