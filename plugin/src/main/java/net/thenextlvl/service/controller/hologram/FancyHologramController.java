package net.thenextlvl.service.controller.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import net.thenextlvl.service.api.hologram.Capability;
import net.thenextlvl.service.api.hologram.CapabilityException;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.hologram.HologramLine;
import net.thenextlvl.service.model.hologram.fancy.FancyBlockHologramLine;
import net.thenextlvl.service.model.hologram.fancy.FancyHologram;
import net.thenextlvl.service.model.hologram.fancy.FancyItemHologramLine;
import net.thenextlvl.service.model.hologram.fancy.FancyTextHologramLine;
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
import java.util.stream.Collectors;

@NullMarked
public class FancyHologramController implements HologramController {
    private final EnumSet<Capability> capabilities = EnumSet.of(
            Capability.BLOCK_LINES,
            Capability.DISPLAY_BACKED,
            Capability.ITEM_LINES,
            Capability.MULTILINE_TEXT,
            Capability.TEXT_LINES
    );

    @Override
    public Hologram createHologram(String name, Location location, Collection<HologramLine<?>> lines) throws CapabilityException {
        var line = lines.iterator().next();
        var manager = FancyHologramsPlugin.get().getHologramManager();
        var hologram = switch (line.getType()) {
            case BLOCK -> {
                var data = new BlockHologramData(name, location);
                data.setBlock(((FancyBlockHologramLine) line).data.getBlock());
                yield manager.create(data);
            }
            case ITEM -> {
                var data = new ItemHologramData(name, location);
                data.setItemStack(((FancyItemHologramLine) line).data.getItemStack());
                yield manager.create(data);
            }
            case TEXT -> {
                var data = new TextHologramData(name, location);
                data.setText(((FancyTextHologramLine) line).data.getText());
                yield manager.create(data);
            }
            case ENTITY -> throw new CapabilityException(
                    "FancyHolograms does not support entity lines",
                    Capability.ENTITY_LINES
            );
        };
        manager.addHologram(hologram);
        return new FancyHologram(hologram);
    }

    @Override
    public HologramLine<BlockData> createLine(BlockData block) {
        var line = new FancyBlockHologramLine(new BlockHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(block);
        return line;
    }

    @Override
    public HologramLine<Component> createLine(Component text) {
        var line = new FancyTextHologramLine(new TextHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(text);
        return line;
    }

    @Override
    public HologramLine<EntityType> createLine(EntityType entity) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support entity lines", Capability.ENTITY_LINES);
    }

    @Override
    public HologramLine<ItemStack> createLine(ItemStack itemStack) {
        var line = new FancyItemHologramLine(new ItemHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(itemStack);
        return line;
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(Player player) {
        var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .filter(hologram -> hologram.isViewer(player))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(World world) {
        var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .filter(hologram -> world.equals(hologram.getData().getLocation().getWorld()))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(String name) {
        var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHologram(name).map(FancyHologram::new);
    }

    @Override
    public @Unmodifiable EnumSet<Capability> getCapabilities() {
        return EnumSet.copyOf(capabilities);
    }

    @Override
    public String getName() {
        return "FancyHolograms";
    }

    @Override
    public boolean hasCapabilities(Collection<Capability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return capabilities.contains(capability);
    }
}
