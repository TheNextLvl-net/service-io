package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramCapability;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.hologram.line.HologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public final class FancyHologramController implements HologramController {
    private final EnumSet<HologramCapability> capabilities = EnumSet.of(
            HologramCapability.BLOCK_LINES,
            HologramCapability.DISPLAY_BACKED,
            HologramCapability.ITEM_LINES,
            HologramCapability.MULTILINE_TEXT,
            HologramCapability.TEXT_LINES
    );

    @Override
    public Hologram createHologram(final String name, final Location location, final Collection<HologramLine<?>> lines) throws CapabilityException {
        final var line = lines.iterator().next();
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        final var hologram = switch (line.getType()) {
            case BLOCK -> {
                final var data = new BlockHologramData(name, location);
                data.setBlock(((FancyBlockHologramLine) line).data.getBlock());
                yield manager.create(data);
            }
            case ITEM -> {
                final var data = new ItemHologramData(name, location);
                data.setItemStack(((FancyItemHologramLine) line).data.getItemStack());
                yield manager.create(data);
            }
            case TEXT -> {
                final var data = new TextHologramData(name, location);
                data.setText(((FancyTextHologramLine) line).data.getText());
                yield manager.create(data);
            }
            case ENTITY -> throw new CapabilityException(
                    "FancyHolograms does not support entity lines",
                    HologramCapability.ENTITY_LINES
            );
        };
        manager.addHologram(hologram);
        return new FancyHologram(hologram);
    }

    @Override
    public HologramLine<BlockData> createLine(final BlockData block) {
        final var line = new FancyBlockHologramLine(new BlockHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(block);
        return line;
    }

    @Override
    public HologramLine<Component> createLine(final Component text) {
        final var line = new FancyTextHologramLine(new TextHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(text);
        return line;
    }

    @Override
    public HologramLine<EntityType> createLine(final EntityType entity) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support entity lines", HologramCapability.ENTITY_LINES);
    }

    @Override
    public HologramLine<ItemStack> createLine(final ItemStack itemStack) {
        final var line = new FancyItemHologramLine(new ItemHologramData(
                "none", new Location(null, 0, 0, 0)
        ));
        line.setContent(itemStack);
        return line;
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final Player player) {
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .filter(hologram -> hologram.isViewer(player))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final World world) {
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHolograms().stream()
                .filter(hologram -> world.equals(hologram.getData().getLocation().getWorld()))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(final String name) {
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        return manager.getHologram(name).map(FancyHologram::new);
    }

    @Override
    public Plugin getPlugin() {
        return FancyHologramsPlugin.get().getPlugin();
    }

    @Override
    public String getName() {
        return "FancyHolograms";
    }

    @Override
    public @Unmodifiable EnumSet<HologramCapability> getCapabilities() {
        return EnumSet.copyOf(this.capabilities);
    }

    @Override
    public boolean hasCapabilities(final Collection<HologramCapability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(final HologramCapability capability) {
        return capabilities.contains(capability);
    }
}
