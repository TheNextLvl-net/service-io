package net.thenextlvl.service.api.hologram;

import net.kyori.adventure.key.Key;
import net.thenextlvl.service.api.capability.Capability;
import org.jspecify.annotations.NullMarked;

/**
 * An enum representing various capabilities a {@link Hologram} might possess.
 * <p>
 * Each capability indicates a specific feature or limitation
 * in how holograms can be represented or interacted with.
 * <p>
 * The enum constant values represent specific capabilities that can be
 * queried or utilized to determine the functionality of particular hologram
 * implementations.
 * <p>
 * Each capability is associated with a unique {@link Key} that acts as an
 * identifier for the capability.
 *
 * @since 2.2.0
 */
@NullMarked
public enum HologramCapability implements Capability {
    /**
     * Represents the capability of using block-oriented lines within a hologram.
     * This capability determines whether holograms can include lines that
     * display blocks as their visual elements.
     */
    BLOCK_LINES(Key.key("capability", "block_lines")),

    /**
     * Represents the capability of using entity-oriented lines within a hologram.
     * This capability determines whether holograms can include lines that display
     * entities as their visual elements.
     */
    ENTITY_LINES(Key.key("capability", "entity_lines")),

    /**
     * Represents the capability of using item-oriented lines within a hologram.
     * This capability determines whether holograms can include lines that display
     * items as their visual elements.
     */
    ITEM_LINES(Key.key("capability", "item_lines")),

    /**
     * Represents the capability of using text-oriented lines within a hologram.
     * This capability determines whether holograms can include lines that display
     * textual content, such as plain or stylized text.
     * <p>
     * It is generally safe to assume that every hologram will have this capability
     */
    TEXT_LINES(Key.key("capability", "text_lines")),

    /**
     * Represents the capability of having multiple lines within a hologram.
     * This capability determines whether holograms can consist of more than
     * a single line.
     */
    MULTILINE(Key.key("capability", "multiline")),

    /**
     * Represents the capability of text lines supporting line breaks within a hologram.
     * This capability determines whether text-oriented hologram lines can include
     * multiple lines of text as part of their visual content.
     * <p>
     * It is mutually exclusive with the {@link #DISPLAY_BACKED} capability.
     */
    MULTILINE_TEXT(Key.key("capability", "multiline_text")),

    /**
     * Represents the capability of holograms being backed by display entities.
     * This capability determines whether holograms are rendered and managed
     * using display entities within the underlying system.
     * <p>
     * It is mutually exclusive with the {@link #MULTILINE_TEXT} capability.
     */
    DISPLAY_BACKED(Key.key("capability", "display_backed"));

    private final Key key;

    HologramCapability(Key key) {
        this.key = key;
    }

    @Override
    public Key key() {
        return this.key;
    }
}
