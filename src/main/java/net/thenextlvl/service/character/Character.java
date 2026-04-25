package net.thenextlvl.service.character;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a managed character exposed by a {@link CharacterController}.
 * <p>
 * A character may or may not be backed by a real server entity, depending on
 * provider capabilities and spawn state.
 */
public interface Character {
    /**
     * Asynchronously teleports the character to the specified location.
     *
     * @param location the {@code Location} to which the character will be teleported
     * @return a {@code CompletableFuture} that resolves to {@code true} if the teleportation was
     * successful, or {@code false} otherwise
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Retrieves the name of the character.
     *
     * @return the name of the character
     */
    String getName();

    /**
     * Checks if this character is persistent.
     *
     * @return {@code true} if this character is persistent, {@code false} otherwise
     */
    boolean isPersistent();

    /**
     * Sets whether this character is persistent.
     *
     * @param persistent {@code true} if this character should be persistent, {@code false} otherwise
     * @return {@code true} if the persistence was changed, {@code false} otherwise
     */
    boolean setPersistent(boolean persistent);

    /**
     * Persists this character.
     *
     * @return {@code true} if the character was persisted, {@code false} otherwise
     */
    boolean persist();

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

    /**
     * Retrieves the character's current location if it is known.
     *
     * @return the current location, or an empty {@code Optional} if it is not
     * available
     */
    Optional<Location> getLocation();

    /**
     * Retrieves the world the character is currently associated with, if known.
     *
     * @return the current world, or an empty {@code Optional} if it is not
     * available
     */
    Optional<World> getWorld();

    /**
     * Retrieves the entity associated with the character.
     * <p>
     * Implementations may return custom or fake {@link Entity} objects even
     * when {@link CharacterCapability#ACTUAL_ENTITIES} is not supported. The
     * capability only guarantees that the returned entity is a real server
     * entity the server can interact with.
     *
     * @return an {@code Optional} containing the associated entity, or an empty {@code Optional}
     * if no entity is associated with the character
     */
    Optional<Entity> getEntity();

    /**
     * Retrieves an unmodifiable set of players currently tracking this character.
     *
     * @return the players currently tracking this character
     */
    @Unmodifiable
    Set<Player> getTrackedBy();

    /**
     * Retrieves an unmodifiable set of players explicitly configured as viewers
     * of this character.
     *
     * @return the configured viewers of this character
     */
    @Unmodifiable
    Set<Player> getViewers();

    /**
     * Adds a player as viewer of this character.
     *
     * @param player the player to add
     * @return {@code true} if the viewer was added, {@code false} otherwise
     */
    boolean addViewer(Player player);

    /**
     * Adds multiple players as viewers of this character.
     *
     * @param players the players to add
     * @return {@code true} if any viewers were added, {@code false} otherwise
     */
    boolean addViewers(Collection<Player> players);

    /**
     * Checks if the given player is tracking this character.
     *
     * @param player the player to check
     * @return {@code true} if the player is tracking this character, {@code false} otherwise
     */
    boolean isTrackedBy(Player player);

    /**
     * Checks if the given player can currently see this character.
     *
     * @param player the player to check
     * @return {@code true} if the player currently qualifies to see this
     * character, {@code false} otherwise
     */
    boolean canSee(Player player);

    /**
     * Checks if this character is visible by default.
     *
     * @return {@code true} if this character is visible by default, {@code false} otherwise
     */
    boolean isVisibleByDefault();

    /**
     * Removes a player from the viewers of this character.
     *
     * @param player the player to remove
     * @return {@code true} if the viewer was removed, {@code false} otherwise
     */
    boolean removeViewer(Player player);

    /**
     * Removes multiple players from the viewers of this character.
     *
     * @param players the players to remove
     * @return {@code true} if any viewers were removed, {@code false} otherwise
     */
    boolean removeViewers(Collection<Player> players);

    /**
     * Retrieves the display range of this character.
     *
     * @return the display range
     */
    double getDisplayRange();

    /**
     * Sets the display range of this character.
     *
     * @param range the new display range
     */
    void setDisplayRange(double range);

    /**
     * Sets whether this character is visible by default.
     *
     * @param visible {@code true} if this character should be visible by default, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     */
    boolean setVisibleByDefault(boolean visible);

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
     * Checks if the character is invulnerable.
     *
     * @return {@code true} if the character is invulnerable, otherwise {@code false}
     */
    boolean isInvulnerable();

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
     * Sets the display name for the character.
     *
     * @param displayName the {@code Component} representing the new display name to be set
     */
    void setDisplayName(Component displayName);

    /**
     * Sets whether the character should be invulnerable.
     *
     * @param invulnerable {@code true} if the character should be invulnerable, {@code false} otherwise
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * Sets the visibility state of the character's tablist entry.
     *
     * @param hidden {@code true} to hide the tablist entry, {@code false} to make it visible
     */
    void setTablistEntryHidden(boolean hidden);
}
