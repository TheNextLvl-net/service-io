package net.thenextlvl.service.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Represents a controller for managing holograms and their related functionality.
 * Provides methods for creating holograms, creating hologram lines with various types of content,
 * and querying the available holograms and supported capabilities.
 * <p>
 * The controller ensures that capabilities of the hologram provider are respected and throws
 * exceptions if unsupported capabilities are used.
 */
@NullMarked
public interface HologramController {
    /**
     * Creates a new hologram with the specified name, location, and lines.
     * This method may throw a {@link CapabilityException} if the required capabilities
     * to create the hologram with the specified lines aren't available.
     *
     * @param name     the name of the hologram to create, used for identification
     * @param location the location where the hologram should be created
     * @param lines    the collection of hologram lines to include in the hologram
     * @return the created {@link Hologram} instance
     * @throws CapabilityException if the required capabilities for creating
     *                             the hologram or its lines aren't present
     */
    Hologram createHologram(String name, Location location, Collection<HologramLine<?>> lines) throws CapabilityException;

    /**
     * Creates a new hologram line with block data as its content.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link Capability#BLOCK_LINES} is not available.
     *
     * @param block the {@code BlockData} to be used as the content of the hologram line
     * @return the created {@link HologramLine} instance containing the specified block data
     * @throws CapabilityException if the required capability for block-oriented hologram lines is not present
     */
    HologramLine<BlockData> createLine(BlockData block) throws CapabilityException;

    /**
     * Creates a new hologram line with text content.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link Capability#TEXT_LINES} is not available.
     *
     * @param text the {@code Component} to be displayed as the content of the hologram line
     * @return the created {@link HologramLine} instance containing the specified text content
     * @throws CapabilityException if the required capability for text-oriented hologram lines is not present
     */
    HologramLine<Component> createLine(Component text) throws CapabilityException;

    /**
     * Creates a new hologram line with an entity as its content.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link Capability#ENTITY_LINES} is not available.
     *
     * @param entity the {@code EntityType} to be displayed as the content of the hologram line
     * @return the created {@code HologramLine} instance containing the specified entity type
     * @throws CapabilityException if the capability for entity-oriented hologram lines is not present
     */
    HologramLine<EntityType> createLine(EntityType entity) throws CapabilityException;

    /**
     * Creates a new hologram line with an item as its content.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link Capability#ITEM_LINES} is not available.
     *
     * @param itemStack the {@code ItemStack} to be displayed as the content of the hologram line
     * @return the created {@link HologramLine} instance containing the specified itemStack
     * @throws CapabilityException if the capability for item-oriented hologram lines is not present
     */
    HologramLine<ItemStack> createLine(ItemStack itemStack) throws CapabilityException;

    /**
     * Retrieves an unmodifiable list of all available holograms.
     *
     * @return an unmodifiable {@link List} containing all {@link Hologram} instances.
     */
    @Unmodifiable
    List<Hologram> getHolograms();

    /**
     * Retrieves an unmodifiable list of holograms associated with the specified player.
     *
     * @param player the player whose associated holograms are to be retrieved
     * @return an unmodifiable list of {@link Hologram} instances associated with the given player
     */
    @Unmodifiable
    List<Hologram> getHolograms(Player player);

    /**
     * Retrieves an unmodifiable list of all holograms present in the specified world.
     *
     * @param world the world for which holograms are to be retrieved
     * @return an unmodifiable {@link List} containing all {@link Hologram} instances in the specified world
     */
    @Unmodifiable
    List<Hologram> getHolograms(World world);

    /**
     * Retrieves an existing hologram by its name.
     *
     * @param name the name of the hologram to be retrieved
     * @return an {@link Optional} containing the {@link Hologram} if a hologram with the given name exists,
     * or an empty {@link Optional} if no such hologram exists
     */
    Optional<Hologram> getHologram(String name);

    /**
     * Retrieves an unmodifiable set of all available capabilities supported by the hologram provider.
     *
     * @return an unmodifiable {@link EnumSet} containing the supported {@link Capability} values
     */
    @Unmodifiable
    EnumSet<Capability> getCapabilities();

    /**
     * Retrieves the name of the hologram provider.
     *
     * @return the name of the hologram provider.
     */
    String getName();

    /**
     * Checks whether all the specified capabilities are supported by the hologram provider.
     *
     * @param capabilities the collection of {@link Capability} instances to verify for support
     * @return {@code true} if all the specified capabilities are supported; {@code false} otherwise
     */
    boolean hasCapabilities(Collection<Capability> capabilities);

    /**
     * Checks whether the specified capability is supported.
     *
     * @param capability the {@link Capability} to verify for support
     * @return {@code true} if the given capability is supported; {@code false} otherwise
     */
    boolean hasCapability(Capability capability);
}
