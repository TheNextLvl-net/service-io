package net.thenextlvl.service.hologram;

import net.thenextlvl.service.Controller;
import net.thenextlvl.service.capability.CapabilityProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a controller for managing holograms and their related functionality.
 * Provides methods for creating holograms and querying available holograms and supported capabilities.
 * <p>
 * The controller ensures that capabilities of the hologram provider are respected and throws
 * exceptions if unsupported capabilities are used.
 */
public interface HologramController extends CapabilityProvider<HologramCapability>, Controller {
    /**
     * Creates a new hologram with the specified name and location.
     *
     * @param name     the name of the hologram to create, used for identification
     * @param location the location where the hologram should be created
     * @return the created {@link Hologram} instance
     * @since 3.0.0
     */
    Hologram createHologram(String name, Location location);

    /**
     * Delete a hologram
     *
     * @param hologram the hologram to delete
     * @return {@code true} if the hologram was deleted, {@code false} otherwise
     * @since 3.0.0
     */
    boolean deleteHologram(Hologram hologram);

    /**
     * Retrieves a stream of all available holograms.
     *
     * @return a stream containing all hologram instances
     * @since 3.0.0
     */
    Stream<Hologram> getHolograms();

    /**
     * Retrieves a stream of holograms associated with the specified player.
     *
     * @param player the player whose associated holograms are to be retrieved
     * @return a stream of hologram instances associated with the given player
     * @since 3.0.0
     */
    Stream<Hologram> getHolograms(Player player);

    /**
     * Retrieves a stream of all holograms present in the specified world.
     *
     * @param world the world for which holograms are to be retrieved
     * @return a stream containing all hologram instances in the specified world
     * @since 3.0.0
     */
    Stream<Hologram> getHolograms(World world);

    /**
     * Retrieves an existing hologram by its name.
     *
     * @param name the name of the hologram to be retrieved
     * @return an {@link Optional} containing the hologram if found, or empty if no such hologram exists
     */
    Optional<Hologram> getHologram(String name);
}
