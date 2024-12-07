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
}
