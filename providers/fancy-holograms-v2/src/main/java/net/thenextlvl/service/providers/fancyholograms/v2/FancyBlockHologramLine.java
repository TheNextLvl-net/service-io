package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FancyBlockHologramLine extends FancyHologramLine<BlockHologramData, BlockData> {
    public FancyBlockHologramLine(final BlockHologramData data) {
        super(data);
    }

    @Override
    public BlockData getContent() {
        return data.getBlock().createBlockData();
    }

    @Override
    public void setContent(final BlockData content) {
        data.setBlock(content.getMaterial());
    }
}
