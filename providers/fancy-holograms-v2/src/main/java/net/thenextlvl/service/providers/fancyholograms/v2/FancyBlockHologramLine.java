package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.BlockHologramData;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FancyBlockHologramLine extends FancyHologramLine<BlockHologramData, BlockData> {
    public FancyBlockHologramLine(BlockHologramData data) {
        super(data);
    }

    @Override
    public BlockData getContent() {
        return data.getBlock().createBlockData();
    }

    @Override
    public void setContent(BlockData content) {
        data.setBlock(content.getMaterial());
    }
}
