package net.thenextlvl.service.controller.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.hologram.HologramLine;
import net.thenextlvl.service.model.hologram.DecentBlockHologramLine;
import net.thenextlvl.service.model.hologram.DecentEntityHologramLine;
import net.thenextlvl.service.model.hologram.DecentHologram;
import net.thenextlvl.service.model.hologram.DecentItemHologramLine;
import net.thenextlvl.service.model.hologram.DecentTextHologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public class DecentHologramController implements HologramController {
    @Override
    public Hologram createHologram(String name, Location location, Collection<HologramLine<?>> lines) {
        var hologram = new DecentHologram(DHAPI.createHologram(name, location, false));
        hologram.addLines(lines);
        return hologram;
    }

    @Override
    public HologramLine<BlockData> createLine(BlockData block) {
        var item = ItemStack.of(block.getMaterial());
        if (block instanceof Skull skull && skull.getPlayerProfile() != null) {
            var profile = ResolvableProfile.resolvableProfile(skull.getPlayerProfile());
            item.setData(DataComponentTypes.PROFILE, profile);
        }
        var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                null, new Location(null, 0, 0, 0),
                "#HEAD:" + HologramItem.fromItemStack(item).getContent()
        );
        return new DecentBlockHologramLine(line, false);
    }

    @Override
    public HologramLine<Component> createLine(Component text) {
        var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                null, new Location(null, 0, 0, 0),
                LegacyComponentSerializer.legacyAmpersand().serialize(text)
        );
        return new DecentTextHologramLine(line);
    }

    @Override
    public HologramLine<EntityType> createLine(EntityType entity) {
        var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                null, new Location(null, 0, 0, 0),
                "#ENTITY:" + entity.name()
        );
        return new DecentEntityHologramLine(line);
    }

    @Override
    public HologramLine<ItemStack> createLine(ItemStack itemStack) {
        var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                null, new Location(null, 0, 0, 0),
                "#ICON:" + HologramItem.fromItemStack(itemStack).getContent()
        );
        return new DecentItemHologramLine(line);
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(Player player) {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .filter(hologram -> hologram.isVisible(player))
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(World world) {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .filter(hologram -> world.equals(hologram.getLocation().getWorld()))
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(String name) {
        return Optional.ofNullable(DHAPI.getHologram(name))
                .map(DecentHologram::new);
    }
}
