package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
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
import java.util.stream.Collectors;

@NullMarked
public final class DecentHologramController implements HologramController {
    private final EnumSet<HologramCapability> capabilities = EnumSet.of(
            HologramCapability.BLOCK_LINES,
            HologramCapability.ENTITY_LINES,
            HologramCapability.ITEM_LINES,
            HologramCapability.TEXT_LINES,
            HologramCapability.MULTILINE,
            HologramCapability.PAGINATION
    );

    @Override
    public Hologram createHologram(final String name, final Location location) {
        return new DecentHologram(DHAPI.createHologram(name, location, false));
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms() {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final Player player) {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .filter(hologram -> hologram.isVisible(player))
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Hologram> getHolograms(final World world) {
        return eu.decentsoftware.holograms.api.holograms.Hologram.getCachedHolograms().stream()
                .filter(hologram -> world.equals(hologram.getLocation().getWorld()))
                .map(DecentHologram::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Hologram> getHologram(final String name) {
        return Optional.ofNullable(DHAPI.getHologram(name))
                .map(DecentHologram::new);
    }

    @Override
    public Plugin getPlugin() {
        return DecentHologramsAPI.get().getPlugin();
    }

    @Override
    public String getName() {
        return "DecentHolograms";
    }

    @Override
    public EnumSet<HologramCapability> getCapabilities() {
        return EnumSet.copyOf(capabilities);
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
