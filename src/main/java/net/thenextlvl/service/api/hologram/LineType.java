package net.thenextlvl.service.api.hologram;

/**
 * Enumeration representing the different types of lines that can be part of a hologram.
 * Each line type corresponds to a specific kind of visual content that a hologram can display.
 *
 * @since 2.2.0
 */
public enum LineType {
    /**
     * Represents a line type within a hologram that displays block-oriented content.
     */
    BLOCK,
    /**
     * Represents a line type within a hologram that displays entity-oriented content.
     */
    ENTITY,
    /**
     * Represents a line type within a hologram that displays item-oriented content.
     */
    ITEM,
    /**
     * Represents a line type within a hologram that displays text-oriented content.
     */
    TEXT
}
