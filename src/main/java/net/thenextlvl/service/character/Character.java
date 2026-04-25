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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a managed character exposed by a {@link CharacterController}.
 * <p>
 * A character may or may not be backed by a real server entity, depending on
 * provider capabilities and spawn state.
 *
 * @since 2.2.0
 */
public interface Character {
    /**
     * Asynchronously teleports the character to the specified location.
     *
     * @param location the target location to teleport the character to
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the character was teleported, {@code false} otherwise
     * @since 2.2.0
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Returns the name of the character.
     *
     * @return the name of the character
     * @since 3.0.0
     */
    String getName();

    /**
     * Checks if this character is persistent.
     *
     * @return {@code true} if this character is persistent, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isPersistent();

    /**
     * Sets whether this character is persistent.
     *
     * @param persistent {@code true} if this character should be persistent, {@code false} otherwise
     * @return {@code true} if the persistence was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setPersistent(boolean persistent);

    /**
     * Persists this character.
     *
     * @return {@code true} if the character was persisted, {@code false} otherwise
     * @since 3.0.0
     */
    boolean persist();

    /**
     * Returns the display name of the character.
     *
     * @return the display name of the character
     * @since 2.2.0
     */
    Component getDisplayName();

    /**
     * Returns the type of entity associated with the character.
     *
     * @return the entity type of the character
     * @since 2.2.0
     */
    EntityType getType();

    /**
     * Returns the location of the character if it is known.
     *
     * @return the location of the character, or empty if not available
     * @since 3.0.0
     */
    Optional<Location> getLocation();

    /**
     * Returns the world of the character if it is known.
     *
     * @return the world of the character, or empty if not available
     * @since 3.0.0
     */
    Optional<World> getWorld();

    /**
     * Returns the entity associated with the character.
     * <p>
     * Implementations may return custom or fake {@link Entity} objects even
     * when {@link CharacterCapability#ACTUAL_ENTITIES} is not supported. The
     * capability only guarantees that the returned entity is a real server
     * entity the server can interact with.
     *
     * @return an optional containing the associated entity, or empty if no
     * entity is associated with the character
     * @since 3.0.0
     */
    Optional<Entity> getEntity();

    /**
     * Returns the players that are tracking this character.
     *
     * @return the players currently tracking this character
     * @since 3.0.0
     */
    @Unmodifiable
    Set<Player> getTrackedBy();

    /**
     * Returns the viewers of this character.
     *
     * @return an unmodifiable set of viewer UUIDs
     * @since 3.0.0
     */
    @Unmodifiable
    Set<UUID> getViewers();

    /**
     * Adds a viewer to this character.
     *
     * @param player the viewer UUID
     * @return {@code true} if the viewer was added, {@code false} otherwise
     * @since 3.0.0
     */
    boolean addViewer(UUID player);

    /**
     * Adds viewers to this character.
     *
     * @param players the viewer UUIDs
     * @return {@code true} if any viewers were added, {@code false} otherwise
     * @since 3.0.0
     */
    boolean addViewers(Collection<UUID> players);

    /**
     * Checks if the given player is tracking this character.
     *
     * @param player the player to check
     * @return {@code true} if the player is tracking this character, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isTrackedBy(Player player);

    /**
     * Checks if this character has the given viewer.
     *
     * @param player the viewer UUID
     * @return {@code true} if the UUID is a viewer of this character, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isViewer(UUID player);

    /**
     * Checks if the given player can currently see this character.
     * <p>
     * This method is different to {@link #isTrackedBy(Player)} in that it checks
     * if the player currently qualifies to see the character.
     *
     * @param player the player
     * @return {@code true} if the given player can see this character, {@code false} otherwise
     * @see #isVisibleByDefault()
     * @see #isViewer(UUID)
     * @see #isTrackedBy(Player)
     * @since 3.0.0
     */
    boolean canSee(Player player);

    /**
     * Checks if this character is visible by default.
     *
     * @return {@code true} if this character is visible by default, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isVisibleByDefault();

    /**
     * Removes a viewer from this character.
     *
     * @param player the viewer UUID
     * @return {@code true} if the viewer was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeViewer(UUID player);

    /**
     * Removes viewers from this character.
     *
     * @param players the viewer UUIDs
     * @return {@code true} if any viewers were removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeViewers(Collection<UUID> players);

    /**
     * Returns the display range of this character.
     *
     * @return the display range
     * @since 3.0.0
     */
    double getDisplayRange();

    /**
     * Sets the display range of this character.
     *
     * @param range the new display range
     * @since 3.0.0
     */
    void setDisplayRange(double range);

    /**
     * Sets whether this character is visible by default.
     *
     * @param visible {@code true} if this character should be visible by default, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setVisibleByDefault(boolean visible);

    /**
     * Despawns the character.
     *
     * @return {@code true} if the character was despawned, {@code false} otherwise
     * @since 2.2.0
     */
    boolean despawn();

    /**
     * Checks if this character is collidable.
     *
     * @return {@code true} if this character is collidable, {@code false} otherwise
     * @since 2.2.0
     */
    boolean isCollidable();

    /**
     * Checks if this character is invulnerable.
     *
     * @return {@code true} if this character is invulnerable, {@code false} otherwise
     * @since 2.2.0
     */
    boolean isInvulnerable();

    /**
     * Checks if this character is spawned.
     *
     * @return {@code true} if this character is spawned, {@code false} otherwise
     * @since 2.2.0
     */
    boolean isSpawned();

    /**
     * Checks if the tablist entry of this character is hidden.
     *
     * @return {@code true} if the tablist entry is hidden, {@code false} otherwise
     * @since 2.2.0
     */
    boolean isTablistEntryHidden();

    /**
     * Respawns the character.
     *
     * @return {@code true} if the character was respawned, {@code false} otherwise
     * @since 2.2.0
     */
    boolean respawn();

    /**
     * Spawns the character at the specified location.
     *
     * @param location the location to spawn the character at
     * @return {@code true} if the character was spawned, {@code false} otherwise
     * @since 2.2.0
     */
    boolean spawn(Location location);

    /**
     * Makes the character look at the given entity.
     *
     * @param entity the entity to look at
     * @since 2.2.0
     */
    void lookAt(Entity entity);

    /**
     * Makes the character look at the given location.
     *
     * @param location the location to look at
     * @since 2.2.0
     */
    void lookAt(Location location);

    /**
     * Permanently removes the character.
     *
     * @since 2.2.0
     */
    void remove();

    /**
     * Sets whether this character is collidable.
     *
     * @param collidable {@code true} if this character should be collidable, {@code false} otherwise
     * @since 2.2.0
     */
    void setCollidable(boolean collidable);

    /**
     * Sets the display name of the character.
     *
     * @param displayName the new display name
     * @since 2.2.0
     */
    void setDisplayName(Component displayName);

    /**
     * Sets whether this character is invulnerable.
     *
     * @param invulnerable {@code true} if this character should be invulnerable, {@code false} otherwise
     * @since 2.2.0
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * Sets whether the tablist entry of this character is hidden.
     *
     * @param hidden {@code true} if the tablist entry should be hidden, {@code false} otherwise
     * @since 2.2.0
     */
    void setTablistEntryHidden(boolean hidden);
}
