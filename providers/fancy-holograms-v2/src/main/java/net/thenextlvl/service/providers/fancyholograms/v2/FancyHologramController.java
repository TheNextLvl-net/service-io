package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramCapability;
import net.thenextlvl.service.api.hologram.HologramController;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public Hologram createHologram(final String name, final Location location) {
        final var manager = FancyHologramsPlugin.get().getHologramManager();
        final var data = new de.oliver.fancyholograms.api.data.TextHologramData(name, location);
        final var hologram = manager.create(data);
        manager.addHologram(hologram);
        return new FancyHologram(hologram);
    }

    @Override
    public boolean deleteHologram(final Hologram hologram) {
        if (hologram instanceof FancyHologram(final de.oliver.fancyholograms.api.hologram.Hologram fancy)) {
            FancyHologramsPlugin.get().getHologramManager().removeHologram(fancy);
            return true;
        } else return false;
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
    public @Unmodifiable Set<HologramCapability> getCapabilities() {
        return Set.copyOf(capabilities);
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
