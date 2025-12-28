package net.thenextlvl.service.hologram.fancy.v3.model;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.HologramCapability;
import net.thenextlvl.service.api.hologram.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public record FancyHologram(com.fancyinnovations.fancyholograms.api.hologram.Hologram hologram) implements Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        hologram().getData().setLocation(location);
        // hologram().refreshForViewersInWorld(); // fixme
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public @Unmodifiable List<HologramLine<?>> getLines() {
        return List.of(switch (hologram().getData().getType()) {
            case BLOCK -> new FancyBlockHologramLine((BlockHologramData) hologram().getData());
            case ITEM -> new FancyItemHologramLine((ItemHologramData) hologram().getData());
            case TEXT -> new FancyTextHologramLine((TextHologramData) hologram().getData());
        });
    }

    @Override
    public boolean addLine(HologramLine<?> line) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public boolean addLine(int index, HologramLine<?> line) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public boolean addLines(Collection<HologramLine<?>> lines) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public boolean removeLine(HologramLine<?> line) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public boolean removeLine(int index) throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public int getLineCount() {
        return 1;
    }

    @Override
    public void remove() {
        FancyHolograms.get().getRegistry().unregister(hologram());
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return getLines().iterator();
    }

    @Override
    public String getName() {
        return hologram().getData().getName();
    }

    @Override
    public boolean isPersistent() {
        return hologram().getData().isPersistent();
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        // FancyHolograms.get().getRegistry().save(); // fixme
        return true;
    }

    @Override
    public void setPersistent(boolean persistent) {
        hologram().getData().setPersistent(true);
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return hologram().getViewers().stream()
                .map(getServer()::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return getServer().getOnlinePlayers().stream()
                .filter(this::canSee)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addViewer(Player player) {
        FancyHolograms.get().getController().refreshHologram(hologram(), player);
        return canSee(player);
    }

    @Override
    public boolean addViewers(Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return hologram().isViewer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return FancyHolograms.get().getController().shouldSeeHologram(hologram(), player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return hologram().getData().getVisibility().equals(Visibility.ALL);
    }

    @Override
    public boolean removeViewer(Player player) {
        FancyHolograms.get().getController().refreshHologram(hologram(), player);
        return canSee(player);
    }

    @Override
    public boolean removeViewers(Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return hologram().getData().getVisibilityDistance();
    }

    @Override
    public void setDisplayRange(double range) {
        hologram().getData().setVisibilityDistance((int) range);
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        hologram().getData().setVisibility(visible ? Visibility.ALL : Visibility.MANUAL);
    }

    @Override
    public Location getLocation() {
        return hologram().getData().getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public double getX() {
        return getLocation().getX();
    }

    @Override
    public double getY() {
        return getLocation().getY();
    }

    @Override
    public double getZ() {
        return getLocation().getZ();
    }

    @Override
    public float getPitch() {
        return getLocation().getPitch();
    }

    @Override
    public float getYaw() {
        return getLocation().getYaw();
    }
}
