package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.line.BlockHologramLine;
import net.thenextlvl.service.api.hologram.line.EntityHologramLine;
import net.thenextlvl.service.api.hologram.line.HologramLine;
import net.thenextlvl.service.api.hologram.line.ItemHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import net.thenextlvl.service.api.hologram.line.TextHologramLine;
import net.thenextlvl.service.api.model.Persistable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Represents a hologram that can display multiple lines of content in a virtual 3D space.
 * This interface provides methods for managing the hologram's lines, visibility, position,
 * and persistence.
 *
 * @since 3.0.0
 */
public interface Hologram extends Persistable, Iterable<HologramLine> {
    /**
     * Sets the name of the hologram.
     *
     * @param name the new hologram name
     * @return {@code true} if the name was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setName(String name);

    /**
     * Returns the location of the hologram.
     *
     * @return the location of the hologram
     */
    Location getLocation();

    /**
     * Returns the world of the hologram.
     *
     * @return the world of the hologram
     */
    World getWorld();

    /**
     * Asynchronously teleports the hologram to the specified location.
     *
     * @param location the target location to teleport the hologram to
     * @return a {@link CompletableFuture} that completes with {@code true}
     * if the hologram was teleported, {@code false} otherwise
     * @since 3.0.0
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Returns all lines of this hologram.
     *
     * @return a stream of hologram lines
     * @since 3.0.0
     */
    Stream<HologramLine> getLines();

    /**
     * Returns the number of lines in this hologram.
     *
     * @return the number of lines
     * @since 3.0.0
     */
    int getLineCount();

    /**
     * Returns the line at the given index.
     *
     * @param index the line index
     * @return the line at the given index, or empty if out of bounds
     * @since 3.0.0
     */
    Optional<HologramLine> getLine(int index);

    /**
     * Returns the line at the given index if the line is of the given type.
     *
     * @param index the line index
     * @param type  the expected line type
     * @param <T>   the line type
     * @return an optional containing the line at the given index of the given type
     * @since 3.0.0
     */
    <T extends HologramLine> Optional<T> getLine(int index, Class<T> type);

    /**
     * Returns the index of the given line.
     *
     * @param line the line to find the index of
     * @return the index of the given line, or {@code -1} if the line is not found
     * @see java.util.List#indexOf(Object)
     * @since 3.0.0
     */
    int getLineIndex(HologramLine line);

    /**
     * Removes the given line from this hologram.
     *
     * @param line the line to remove
     * @return {@code true} if the line was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeLine(HologramLine line);

    /**
     * Removes the line at the given index from this hologram.
     *
     * @param index the index of the line to remove
     * @return {@code true} if the line was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeLine(int index);

    /**
     * Removes all the given lines from this hologram.
     *
     * @param lines the lines to remove
     * @return {@code true} if any lines were removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeLines(Collection<HologramLine> lines);

    /**
     * Removes all lines from this hologram.
     *
     * @return {@code true} if any lines were removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean clearLines();

    /**
     * Checks if the given line is in this hologram.
     *
     * @param line the line to check
     * @return {@code true} if this hologram has the given line, {@code false} otherwise
     * @since 3.0.0
     */
    boolean hasLine(HologramLine line);

    /**
     * Moves the line at the given index to the given index.
     *
     * @param from source index
     * @param to   destination index
     * @return {@code true} if the line was moved, {@code false} otherwise
     * @since 3.0.0
     */
    boolean moveLine(int from, int to);

    /**
     * Swaps the lines at the given indices.
     *
     * @param line1 first line index
     * @param line2 second line index
     * @return {@code true} if the lines were swapped, {@code false} otherwise
     * @since 3.0.0
     */
    boolean swapLines(int line1, int line2);

    /**
     * Adds an entity line to this hologram.
     *
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @throws CapabilityException      if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine addEntityLine(EntityType entityType) throws IllegalArgumentException, CapabilityException;

    /**
     * Adds an entity line to this hologram.
     *
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @throws CapabilityException      if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine addEntityLine(Class<? extends Entity> entityType) throws IllegalArgumentException, CapabilityException;

    /**
     * Adds an entity line to this hologram at the given index.
     *
     * @param index      the line index
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine addEntityLine(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Adds an entity line to this hologram at the given index.
     *
     * @param index      the line index
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine addEntityLine(int index, Class<? extends Entity> entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Adds a block line to this hologram.
     *
     * @return a new block hologram line
     * @throws CapabilityException if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine addBlockLine() throws CapabilityException;

    /**
     * Adds a block line to this hologram at the given index.
     *
     * @param index the line index
     * @return a new block hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine addBlockLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Adds an item line to this hologram.
     *
     * @return a new item hologram line
     * @throws CapabilityException if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine addItemLine() throws CapabilityException;

    /**
     * Adds an item line to this hologram at the given index.
     *
     * @param index the line index
     * @return a new item hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine addItemLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Adds a text line to this hologram.
     *
     * @return a new text hologram line
     * @throws CapabilityException if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine addTextLine() throws CapabilityException;

    /**
     * Adds a text line to this hologram at the given index.
     *
     * @param index the line index
     * @return a new text hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine addTextLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Adds a paged line to this hologram.
     *
     * @return a new paged hologram line
     * @throws CapabilityException if the {@link HologramCapability#PAGINATION} capability is not available
     * @since 3.0.0
     */
    PagedHologramLine addPagedLine() throws CapabilityException;

    /**
     * Adds a paged line to this hologram at the given index.
     *
     * @param index the line index
     * @return a new paged hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#PAGINATION} capability is not available
     * @since 3.0.0
     */
    PagedHologramLine addPagedLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets a paged line at the given index.
     *
     * @param index the line index
     * @return a new paged hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#PAGINATION} capability is not available
     * @since 3.0.0
     */
    PagedHologramLine setPagedLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets an entity line at the given index.
     *
     * @param index      the line index
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine setEntityLine(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets an entity line at the given index.
     *
     * @param index      the line index
     * @param entityType the entity type
     * @return a new entity hologram line
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine setEntityLine(int index, Class<? extends Entity> entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets a block line at the given index.
     *
     * @param index the line index
     * @return a new block hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine setBlockLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets an item line at the given index.
     *
     * @param index the line index
     * @return a new item hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine setItemLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets a text line at the given index.
     *
     * @param index the line index
     * @return a new text hologram line
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine setTextLine(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Returns the view permission of this hologram.
     *
     * @return the view permission, or empty if not set
     * @since 3.0.0
     */
    Optional<String> getViewPermission();

    /**
     * Sets the view permission of this hologram.
     *
     * @param permission the view permission, or null to unset
     * @return {@code true} if the view permission was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setViewPermission(@Nullable String permission);

    /**
     * Returns the players that are tracking this hologram.
     *
     * @return a stream of players tracking this hologram
     * @since 3.0.0
     */
    Stream<Player> getTrackedBy();

    /**
     * Returns the viewers of this hologram.
     *
     * @return an unmodifiable set of viewer UUIDs
     * @since 3.0.0
     */
    @Unmodifiable
    Set<UUID> getViewers();

    /**
     * Adds a viewer to this hologram.
     *
     * @param player the viewer UUID
     * @return {@code true} if the viewer was added, {@code false} otherwise
     * @since 3.0.0
     */
    boolean addViewer(UUID player);

    /**
     * Adds viewers to this hologram.
     *
     * @param players the viewer UUIDs
     * @return {@code true} if any viewers were added, {@code false} otherwise
     * @since 3.0.0
     */
    boolean addViewers(Collection<UUID> players);

    /**
     * Removes a viewer from this hologram.
     *
     * @param player the viewer UUID
     * @return {@code true} if the viewer was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeViewer(UUID player);

    /**
     * Removes viewers from this hologram.
     *
     * @param players the viewer UUIDs
     * @return {@code true} if any viewers were removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removeViewers(Collection<UUID> players);

    /**
     * Checks if this hologram has the given viewer.
     *
     * @param player the viewer UUID
     * @return {@code true} if the UUID is a viewer of this hologram, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isViewer(UUID player);

    /**
     * Checks if the given player can see this hologram.
     * <p>
     * This method is different to {@link #isTrackedBy(Player)} in that it checks
     * if the player has the permission to see the hologram.
     *
     * @param player the player
     * @return {@code true} if the given player can see this hologram, {@code false} otherwise
     * @see #getViewPermission()
     * @see #isVisibleByDefault()
     * @see #isViewer(UUID)
     * @see #isTrackedBy(Player)
     * @since 3.0.0
     */
    boolean canSee(Player player);

    /**
     * Checks if this hologram is visible by default.
     *
     * @return {@code true} if this hologram is visible by default, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isVisibleByDefault();

    /**
     * Sets whether this hologram is visible by default.
     *
     * @param visible {@code true} if this hologram should be visible by default, {@code false} otherwise
     * @return {@code true} if the visibility was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setVisibleByDefault(boolean visible);

    /**
     * Checks if this hologram is spawned for the given player.
     *
     * @param player the player to check for
     * @return {@code true} if this hologram is spawned for the given player, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isTrackedBy(Player player);

    /**
     * Completely removes this hologram instance from both storage and cache.
     * <p>
     * This method permanently deletes the hologram, ensuring that
     * it is no longer available for retrieval or interaction.
     */
    void remove();
}
