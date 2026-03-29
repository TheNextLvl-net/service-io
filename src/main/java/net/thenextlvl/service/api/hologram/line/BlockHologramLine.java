package net.thenextlvl.service.api.hologram.line;

import net.thenextlvl.service.api.hologram.HologramCapability;
import org.bukkit.block.data.BlockData;

/**
 * Represents a line type within a hologram that displays a block.
 *
 * @see HologramCapability#BLOCK_LINES
 * @since 3.0.0
 */
public interface BlockHologramLine extends StaticHologramLine {
    /**
     * Gets the displayed block.
     *
     * @return the displayed block
     * @since 3.0.0
     */
    BlockData getBlock();

    /**
     * Sets the displayed block.
     *
     * @param block the new block
     * @return {@code true} if the block was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setBlock(BlockData block);
}
