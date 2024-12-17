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
import java.util.List;
import java.util.Optional;

@NullMarked
public interface HologramController {
    Hologram createHologram(String name, Location location, Collection<HologramLine<?>> lines);

    HologramLine<BlockData> createLine(BlockData block);

    HologramLine<Component> createLine(Component text);

    HologramLine<EntityType> createLine(EntityType entity);

    HologramLine<ItemStack> createLine(ItemStack itemStack);

    @Unmodifiable
    List<Hologram> getHolograms();

    @Unmodifiable
    List<Hologram> getHolograms(Player player);

    @Unmodifiable
    List<Hologram> getHolograms(World world);

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
