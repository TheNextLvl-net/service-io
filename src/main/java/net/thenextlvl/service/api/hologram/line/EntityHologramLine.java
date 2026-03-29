package net.thenextlvl.service.api.hologram.line;

import net.thenextlvl.service.api.hologram.HologramCapability;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Represents a line type within a hologram that displays an entity.
 *
 * @see HologramCapability#ENTITY_LINES
 * @since 3.0.0
 */
public interface EntityHologramLine extends StaticHologramLine {
    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType the new entity type
     * @return {@code true} if the entity type was changed, {@code false} otherwise
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 3.0.0
     */
    boolean setEntityType(EntityType entityType) throws IllegalArgumentException;

    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType the new entity type
     * @return {@code true} if the entity type was changed, {@code false} otherwise
     * @throws IllegalArgumentException if the entity type is not valid
     * @since 3.0.0
     */
    boolean setEntityType(Class<? extends Entity> entityType) throws IllegalArgumentException;

    /**
     * Gets the scale of the entity.
     *
     * @return the scale
     * @since 3.0.0
     */
    double getScale();

    /**
     * Sets the scale of the entity.
     *
     * @param scale the new scale
     * @return {@code true} if the scale was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setScale(double scale);
}
