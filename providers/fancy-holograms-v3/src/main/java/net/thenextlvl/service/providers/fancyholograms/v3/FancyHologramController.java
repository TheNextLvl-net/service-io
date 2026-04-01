package net.thenextlvl.service.providers.fancyholograms.v3;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.HologramController;
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
        final var manager = FancyHolograms.get().getHologramFactory();
        final var data = new TextHologramData(name, location);
        final var hologram = manager.apply(data);
        FancyHolograms.get().getRegistry().register(hologram);
        return new FancyHologram(hologram);
    }

    @Override
    public boolean deleteHologram(final Hologram hologram) {
        if (hologram instanceof FancyHologram(final com.fancyinnovations.fancyholograms.api.hologram.Hologram fancy)) {
            if (!FancyHolograms.get().getRegistry().unregister(fancy)) return false;
            FancyHolograms.get().getController().hideHologramFrom(fancy, hologram.getTrackedBy().toArray(Player[]::new));
            return true;
        } else return false;
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        final var registry = FancyHolograms.get().getRegistry();
        return registry.getAll().stream()
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final Player player) {
        final var registry = FancyHolograms.get().getRegistry();
        return registry.getAll().stream()
                .filter(hologram -> hologram.isViewer(player))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final World world) {
        final var registry = FancyHolograms.get().getRegistry();
        return registry.getAll().stream()
                .filter(hologram -> world.equals(hologram.getData().getLocation().getWorld()))
                .map(FancyHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(final String name) {
        final var registry = FancyHolograms.get().getRegistry();
        return registry.get(name).map(FancyHologram::new);
    }

    @Override
    public Plugin getPlugin() {
        return FancyHolograms.get().getPlugin();
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
