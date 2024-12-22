package net.thenextlvl.service.api.npc;

import net.kyori.adventure.text.Component;
import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a character in a world that is associated with an entity and provides various
 * functionalities such as spawning, despawning, teleportation, and state management.
 *
 * @param <T> the type of the entity associated with this character
 */
@NullMarked
public interface Character<T extends Entity> extends Persistable, Viewable {
    /**
     * Asynchronously teleports the character to the specified location.
     *
     * @param location the {@code Location} to which the character will be teleported
     * @return a {@code CompletableFuture} that resolves to {@code true} if the teleportation was
     * successful, or {@code false} otherwise
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Retrieves the display name of the character.
     *
     * @return the {@code Component} representing the display name of the character
     */
    Component getDisplayName();

    /**
     * Retrieves the type of entity associated with the character.
     *
     * @return the entity type of the character
     */
    EntityType getType();

    @Override
    @Nullable
    Location getLocation();

    /**
     * Retrieves the entity associated with the character.
     *
     * @return an {@code Optional} containing the associated entity, or an empty {@code Optional}
     * if no entity is associated with the character.
     */
    Optional<T> getEntity();

    @Override
    @Nullable
    World getWorld();

    /**
     * Despawns the character, effectively removing it from the world or rendering it inactive.
     *
     * @return {@code true} if the character was successfully despawned, otherwise {@code false}
     */
    boolean despawn();

    /**
     * Checks if the character is collidable, meaning it can interact physically with other entities.
     *
     * @return {@code true} if the character is collidable, otherwise {@code false}
     */
    boolean isCollidable();

    /**
     * Checks if the character can take damage.
     * This method requires the provider to support the {@link CharacterCapability#HEALTH} capability.
     *
     * @return {@code true} if the character is damageable, otherwise {@code false}
     */
    boolean isDamageable();

    /**
     * Checks if the character is currently spawned in the world.
     *
     * @return {@code true} if the character is spawned, otherwise {@code false}
     */
    boolean isSpawned();

    /**
     * Checks whether the tablist entry for the character is currently hidden.
     *
     * @return {@code true} if the tablist entry is hidden, otherwise {@code false}
     */
    boolean isTablistEntryHidden();

    /**
     * Respawns the character, bringing it back to the world if it was previously despawned.
     *
     * @return {@code true} if the character was successfully respawned, otherwise {@code false}
     */
    boolean respawn();

    /**
     * Spawns the character at the specified location.
     *
     * @param location the {@code Location} where the character will be spawned
     * @return {@code true} if the character was successfully spawned, otherwise {@code false}
     */
    boolean spawn(Location location);

    /**
     * Adjusts the character's orientation to face the given entity.
     *
     * @param entity the {@code Entity} that the character should face
     */
    void lookAt(Entity entity);

    /**
     * Adjusts the character's orientation to face the specified location.
     *
     * @param location the {@code Location} that the character should face
     */
    void lookAt(Location location);

    /**
     * Permanently removes the character, rendering it inaccessible and removing all associated data.
     * After invoking this method, the character can't be respawned or interacted with.
     */
    void remove();

    /**
     * Sets whether the character is collidable, controlling its ability to physically interact
     * with other entities in the world.
     *
     * @param collidable {@code true} to make the character collidable, allowing physical interactions
     */
    void setCollidable(boolean collidable);

    /**
     * Sets whether the character can take damage.
     * This method requires the provider to support the {@link CharacterCapability#HEALTH} capability.
     *
     * @param damageable {@code true} if the character should be able to take damage, {@code false} otherwise
     */
    void setDamageable(boolean damageable);

    /**
     * Sets the display name for the character.
     *
     * @param displayName the {@code Component} representing the new display name to be set
     */
    void setDisplayName(Component displayName);

    /**
     * Sets the visibility state of the character's tablist entry.
     *
     * @param hidden {@code true} to hide the tablist entry, {@code false} to make it visible
     */
    void setTablistEntryHidden(boolean hidden);
}
