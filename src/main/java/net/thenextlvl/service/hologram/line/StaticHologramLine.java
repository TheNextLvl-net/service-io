package net.thenextlvl.service.hologram.line;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that is static and does not change over time.
 * <p>
 * Implementations that are backed by display entities may additionally implement
 * {@link DisplayHologramLine} to provide access to display-entity specific properties.
 *
 * @see BlockHologramLine
 * @see EntityHologramLine
 * @see ItemHologramLine
 * @see TextHologramLine
 * @see DisplayHologramLine
 * @since 3.0.0
 */
public interface StaticHologramLine extends HologramLine {
    /**
     * Gets the class of the entity representing this line.
     *
     * @return entity class
     * @since 3.0.0
     */
    @Contract(pure = true)
    Class<? extends Entity> getEntityClass();

    /**
     * Gets the entity type of the entity representing this line.
     *
     * @return entity type
     * @since 3.0.0
     */
    @Contract(pure = true)
    EntityType getEntityType();

    /**
     * Gets whether this line is glowing.
     *
     * @return true if this line is glowing
     * @since 3.0.0
     */
    boolean isGlowing();

    /**
     * Sets whether this line is glowing.
     *
     * @param glowing true if this line should glow
     * @return {@code true} if the glowing state was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setGlowing(boolean glowing);

    /**
     * Gets the glow color of this line.
     *
     * @return the glow color, or empty if not set
     * @since 3.0.0
     */
    Optional<TextColor> getGlowColor();

    /**
     * Sets the glow color of this line.
     *
     * @param color the new glow color, or null to unset
     * @return {@code true} if the glow color was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setGlowColor(@Nullable TextColor color);

    /**
     * Gets the billboard setting of this line.
     * <p>
     * The billboard setting controls the automatic rotation of the line to face the player.
     *
     * @return the billboard setting
     * @since 3.0.0
     */
    Display.Billboard getBillboard();

    /**
     * Sets the billboard setting of this line.
     * <p>
     * The billboard setting controls the automatic rotation of the line to face the player.
     *
     * @param billboard the new billboard setting
     * @return {@code true} if the billboard setting was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setBillboard(Display.Billboard billboard);

    /**
     * Gets the parent paged line if this line is part of a paged line.
     *
     * @return the parent paged line, or empty if this line is not part of a paged line
     * @since 3.0.0
     */
    Optional<PagedHologramLine> getParentLine();

    /**
     * Gets the offset of this line relative to the hologram's position.
     *
     * @return a copy of the offset
     * @since 3.0.0
     */
    Vector3f getOffset();

    /**
     * Sets the offset of this line relative to the hologram's position.
     *
     * @param offset the new offset
     * @return {@code true} if the offset was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setOffset(Vector3f offset);
}
