package net.thenextlvl.service.api.character;

import net.kyori.adventure.key.Key;
import net.thenextlvl.service.api.capability.Capability;
import org.jspecify.annotations.NullMarked;

import java.lang.Character;

/**
 * An enum representing various capabilities a {@link Character} might possess.
 * <p>
 * Each capability indicates a specific feature or limitation
 * in how characters can be represented or interacted with.
 * <p>
 * The enum constant values represent specific capabilities that can be
 * queried or utilized to determine the functionality of particular
 * implementations.
 * <p>
 * Each capability is associated with a unique {@link Key} that acts as an
 * identifier for the capability.
 */
@NullMarked
public enum CharacterCapability implements Capability {
    /**
     * Represents the capability for a character to have a health attribute.
     * <p>
     * This capability indicates that the character can make use of features related
     * to health, such as taking damage or recovering health.
     */
    HEALTH(Key.key("capability", "health")),

    /**
     * Represents the capability for a character to handle
     * interactions such as left, and right-click actions.
     */
    INTERACTIONS(Key.key("capability", "interactions")),

    /**
     * Represents the capability for supporting non-player entities such as pigs or zombies.
     * This indicates whether the provider can handle entity types beyond player characters.
     */
    NON_PLAYER_ENTITIES(Key.key("capability", "non_player_entities")),

    /**
     * Represents the capability to utilize actual entity objects.
     * <p>
     * This capability indicates that the character provider supports using real, tangible entities
     * within the game world.
     * Such entities are recognized by the server and may be subject to various
     * physical interactions, such as collisions, gravity, or other in-game mechanics.
     */
    ACTUAL_ENTITIES(Key.key("capability", "actual_entities"));

    private final Key key;

    CharacterCapability(Key key) {
        this.key = key;
    }

    @Override
    public Key key() {
        return this.key;
    }
}
