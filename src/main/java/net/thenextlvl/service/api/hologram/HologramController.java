package net.thenextlvl.service.api.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface HologramController {
    Hologram createHologram(Location location, HologramLine<?>... lines);

    HologramLine<BlockData> createLine(BlockData block);

    HologramLine<Component> createLine(Component text);

    HologramLine<Entity> createLine(Entity entity);

    HologramLine<ItemStack> createLine(ItemStack itemStack);

    @Unmodifiable
    List<Hologram> getHolograms();

    @Unmodifiable
    List<Hologram> getHolograms(Player player);

    @Unmodifiable
    List<Hologram> getHolograms(World world);
}
