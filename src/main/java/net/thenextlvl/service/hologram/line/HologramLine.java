package net.thenextlvl.service.hologram.line;

import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.LineType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line within a hologram.
 * <p>
 * Hologram lines form a type hierarchy based on their content and behavior:
 * <ul>
 *     <li>{@link StaticHologramLine} — a line with fixed, non-cycling content
 *         <ul>
 *             <li>{@link BlockHologramLine} — displays a block</li>
 *             <li>{@link EntityHologramLine} — displays an entity</li>
 *             <li>{@link ItemHologramLine} — displays an item</li>
 *             <li>{@link TextHologramLine} — displays text</li>
 *         </ul>
 *     </li>
 *     <li>{@link PagedHologramLine} — a line that cycles through multiple pages of static content</li>
 * </ul>
 * <p>
 * Some hologram providers are backed by display entities (e.g., {@code TextDisplay}, {@code ItemDisplay}).
 * In that case, the line implementations may additionally implement {@link DisplayHologramLine},
 * which provides access to display-entity specific properties such as transformations, interpolation,
 * shadow, brightness, and view range.
 * <p>
 * Whether a provider is display-backed can be checked using the
 * {@link HologramCapability#DISPLAY_BACKED} capability or by testing {@code line instanceof DisplayHologramLine}.
 *
 * @see StaticHologramLine
 * @see PagedHologramLine
 * @see DisplayHologramLine
 * @since 3.0.0
 */
public interface HologramLine {
    /**
     * Gets the hologram this line belongs to.
     *
     * @return the hologram
     * @since 3.0.0
     */
    Hologram getHologram();

    /**
     * Gets the type of this line.
     *
     * @return the line type
     * @since 3.0.0
     */
    LineType getType();

    /**
     * Gets the world of this line.
     *
     * @return the world
     * @since 3.0.0
     */
    World getWorld();

    /**
     * Returns the view permission of this line.
     *
     * @return the view permission, or empty if not set
     * @since 3.0.0
     */
    Optional<String> getViewPermission();

    /**
     * Sets the view permission of this line.
     *
     * @param permission the view permission, or null to unset
     * @return {@code true} if the view permission was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setViewPermission(@Nullable String permission);

    /**
     * Checks if the given player can see this line.
     *
     * @param player the player
     * @return {@code true} if the given player can see this line, {@code false} otherwise
     * @see #getViewPermission()
     * @since 3.0.0
     */
    boolean canSee(Player player);
}
