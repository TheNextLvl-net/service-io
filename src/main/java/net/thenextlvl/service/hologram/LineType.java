package net.thenextlvl.service.hologram;

import net.thenextlvl.service.hologram.line.BlockHologramLine;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import net.thenextlvl.service.hologram.line.TextHologramLine;

/**
 * Enumeration representing the different types of lines that can be part of a hologram.
 * Each line type corresponds to a specific kind of visual content that a hologram can display.
 */
public enum LineType {
    /**
     * Represents a line type within a hologram that displays block-oriented content.
     *
     * @see BlockHologramLine
     */
    BLOCK,
    /**
     * Represents a line type within a hologram that displays entity-oriented content.
     *
     * @see EntityHologramLine
     */
    ENTITY,
    /**
     * Represents a line type within a hologram that displays item-oriented content.
     *
     * @see ItemHologramLine
     */
    ITEM,
    /**
     * Represents a line type within a hologram that displays text-oriented content.
     *
     * @see TextHologramLine
     */
    TEXT,
    /**
     * Represents a line type within a hologram that cycles through multiple pages.
     * Each page can be of any line type (text, item, block, entity).
     *
     * @see PagedHologramLine
     * @since 3.0.0
     */
    PAGED
}
