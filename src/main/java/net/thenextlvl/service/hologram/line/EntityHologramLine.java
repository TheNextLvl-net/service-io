package net.thenextlvl.service.hologram.line;

import net.thenextlvl.service.hologram.HologramCapability;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Optional;

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
     * @since 3.0.0
     */
    boolean setEntityType(EntityType entityType);

    /**
     * Sets the entity type of the hologram line.
     *
     * @param entityType the new entity type
     * @return {@code true} if the entity type was changed, {@code false} otherwise
     * @since 3.0.0
     */
    default boolean setEntityType(final Class<? extends Entity> entityType) {
        return getEntityType(entityType).map(this::setEntityType).orElse(false);
    }

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

    static Optional<EntityType> getEntityType(final Class<? extends Entity> entityClass) throws IllegalArgumentException {
        return Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null)
                .filter(type -> type.getEntityClass().isAssignableFrom(entityClass))
                .findAny();
    }
}
