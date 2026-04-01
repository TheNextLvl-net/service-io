package net.thenextlvl.service.hologram.line;

import net.thenextlvl.service.hologram.HologramCapability;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Represents a line type within a hologram that displays an item.
 *
 * @see HologramCapability#ITEM_LINES
 * @since 3.0.0
 */
public interface ItemHologramLine extends StaticHologramLine {
    /**
     * Gets the displayed item stack.
     *
     * @return the displayed item stack
     * @since 3.0.0
     */
    ItemStack getItemStack();

    /**
     * Sets the displayed item stack.
     *
     * @param item the new item stack
     * @return {@code true} if the item stack was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setItemStack(@Nullable ItemStack item);

    /**
     * Checks if this item hologram line is a player head.
     * <p>
     * The line will render the item as the viewing player's head if true.
     * <p>
     * This takes precedence over {@link #setItemStack(ItemStack)}.
     *
     * @return {@code true} if this item hologram line is a player head
     * @since 3.0.0
     */
    boolean isPlayerHead();

    /**
     * Sets if this item hologram line is a player head.
     * <p>
     * The line will render the item as the viewing player's head if true.
     *
     * @param playerHead {@code true} if this item hologram line is a player head
     * @return {@code true} if the player head status was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setPlayerHead(boolean playerHead);

    /**
     * Gets the item display transform for this entity.
     * <p>
     * Defaults to {@link ItemDisplay.ItemDisplayTransform#FIXED}.
     *
     * @return the item display transform
     * @since 3.0.0
     */
    ItemDisplay.ItemDisplayTransform getItemDisplayTransform();

    /**
     * Sets the item display transform for this entity.
     *
     * @param display the new display transform
     * @return {@code true} if the item display transform was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setItemDisplayTransform(ItemDisplay.ItemDisplayTransform display);
}
