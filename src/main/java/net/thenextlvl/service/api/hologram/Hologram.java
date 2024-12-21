package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a hologram object that can display multiple persistent and positional text or elements
 * in a virtual 3D space, providing interactivity functionality such as visibility and teleportation.
 * This interface incorporates persistence, viewability, and iterable capabilities.
 */
@NullMarked
public interface Hologram extends Persistable, Viewable, Iterable<HologramLine<?>> {
    /**
     * Asynchronously teleports the hologram to the specified location.
     *
     * @param location the target location to teleport the hologram to
     * @return a CompletableFuture which completes with the teleportation success
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Retrieves an unmodifiable list of all lines associated with the hologram.
     * Each line represents a distinct component of the hologram's visual content,
     * such as text, items, blocks, or entities, and can have specific positional
     * and display attributes.
     *
     * @return an unmodifiable list containing all hologram lines, where each
     * element is an instance of {@link HologramLine}.
     */
    @Unmodifiable
    List<HologramLine<?>> getLines();

    @Override
    Location getLocation();

    @Override
    World getWorld();

    /**
     * Adds a single line to the hologram's content.
     * This method may only be used if the hologram supports the capability for multiple lines,
     * as indicated by {@link HologramCapability#MULTILINE}.
     *
     * @param line the {@link HologramLine} to be added; this line represents a component
     *             of the hologram's visual content such as text, items, blocks, or entities
     * @return true if the line was successfully added, false otherwise
     * @throws CapabilityException if the hologram doesn't support the {@link HologramCapability#MULTILINE}
     */
    boolean addLine(HologramLine<?> line) throws CapabilityException;

    /**
     * Inserts a hologram line at the specified index of the hologram.
     * The provided index determines the placement of the new line, shifting subsequent lines down by one position.
     * This method requires the hologram to support the {@link HologramCapability#MULTILINE} capability.
     *
     * @param index the position at which to insert the hologram line; must be within
     *              the valid range of current hologram lines (zero-to-size inclusive)
     * @param line  the {@link HologramLine} to be added; this line represents a component
     *              of the hologram's visual content such as text, items, blocks, or entities
     * @return true if the line was successfully added at the specified index, false otherwise
     * @throws CapabilityException if the hologram doesn't support the {@link HologramCapability#MULTILINE}
     *                             capability
     */
    boolean addLine(int index, HologramLine<?> line) throws CapabilityException;

    /**
     * Adds a collection of hologram lines to the hologram's content.
     * This method requires the hologram to support the capability for multiple lines,
     * as indicated by {@link HologramCapability#MULTILINE}.
     *
     * @param lines the collection of {@link HologramLine} instances to be added; each line represents
     *              a distinct component of the hologram's visual content such as text, items, blocks,
     *              or entities
     * @return true if any line was added, false otherwise
     * @throws CapabilityException if the hologram doesn't support the {@link HologramCapability#MULTILINE} capability
     */
    boolean addLines(Collection<HologramLine<?>> lines) throws CapabilityException;

    /**
     * Removes a specified hologram line from the hologram.
     * This method requires the hologram to support the {@link HologramCapability#MULTILINE} capability.
     *
     * @param line the {@link HologramLine} to be removed; this line represents a component
     *             of the hologram's visual content such as text, items, blocks, or entities
     * @return true if the line was successfully removed, false otherwise
     * @throws CapabilityException if the hologram doesn't support the {@link HologramCapability#MULTILINE} capability
     */
    boolean removeLine(HologramLine<?> line) throws CapabilityException;

    /**
     * Removes the hologram line at the specified index.
     * This method requires the hologram to support the {@link HologramCapability#MULTILINE} capability.
     *
     * @param index the position of the line to remove; must be within the valid range of current hologram lines
     * @return true if the line was successfully removed, false otherwise
     * @throws CapabilityException if the hologram doesn't support the {@link HologramCapability#MULTILINE} capability
     */
    boolean removeLine(int index) throws CapabilityException;

    /**
     * Retrieves the total number of lines associated with the hologram.
     *
     * @return the number of lines in the hologram, where each line represents a distinct
     * component of the hologram's visual content, such as text, items, blocks, or entities.
     */
    int getLineCount();

    /**
     * Completely removes this hologram instance from both storage and cache.
     * <p>
     * This method is intended to permanently delete the hologram, ensuring that
     * it is no longer available for retrieval or interaction.
     */
    void remove();
}
