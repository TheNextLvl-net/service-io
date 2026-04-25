package net.thenextlvl.service.character;

import net.thenextlvl.service.capability.Capability;

/**
 * Capabilities exposed by a {@link CharacterController} implementation.
 * <p>
 * Capabilities describe optional parts of the character API that may not be
 * available for every provider.
 *
 * @since 2.2.0
 */
public enum CharacterCapability implements Capability {
    /**
     * Supports health-like behavior such as taking damage, regenerating, and
     * emitting related events.
     * <p>
     * This capability does not control whether a character can be marked
     * invulnerable.
     *
     * @since 2.2.0
     */
    HEALTH,

    /**
     * Supports player interaction events such as left-click and right-click
     * actions.
     *
     * @since 2.2.0
     */
    INTERACTIONS,

    /**
     * Supports creating characters backed by entity types other than
     * {@link org.bukkit.entity.EntityType#PLAYER}.
     *
     * @since 2.2.0
     */
    NON_PLAYER_ENTITIES,

    /**
     * Supports backing characters with real server entities that exist in the
     * world and can be interacted with by the server.
     * <p>
     * Implementations may still return custom or fake {@link
     * org.bukkit.entity.Entity} instances without this capability.
     *
     * @since 2.2.0
     */
    ACTUAL_ENTITIES
}
