package net.thenextlvl.service.model.hologram.decent;

import eu.decentsoftware.holograms.api.DHAPI;
import net.thenextlvl.service.api.hologram.Hologram;
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
public record DecentHologram(eu.decentsoftware.holograms.api.holograms.Hologram hologram) implements Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        return CompletableFuture.supplyAsync(() -> {
            DHAPI.moveHologram(hologram(), location);
            return true;
        });
    }

    @Override
    public @Unmodifiable List<HologramLine<?>> getLines() {
        return hologram().getPage(0).getLines().stream()
                .map(line -> switch (line.getType()) {
                    case TEXT -> new DecentTextHologramLine(line);
                    case HEAD -> new DecentBlockHologramLine(line, false);
                    case SMALLHEAD -> new DecentBlockHologramLine(line, true);
                    case ENTITY -> new DecentEntityHologramLine(line);
                    case ICON -> new DecentItemHologramLine(line);
                    case UNKNOWN -> throw new IllegalStateException("Unknown line type: " + line);
                }).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean addLine(HologramLine<?> line) {
        if (!(line instanceof DecentHologramLine<?> decentLine)) return false;
        line.getLocation().setWorld(getWorld());
        return hologram().getPage(0).addLine(decentLine.line);
    }

    @Override
    public boolean addLine(int index, HologramLine<?> line) {
        if (!(line instanceof DecentHologramLine<?> decentLine)) return false;
        return hologram().getPage(0).insertLine(index, decentLine.line);
    }

    @Override
    public boolean addLines(Collection<HologramLine<?>> lines) {
        return lines.stream().map(this::addLine).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeLine(HologramLine<?> line) {
        var index = getLines().indexOf(line);
        return index >= 0 && removeLine(index);
    }

    @Override
    public boolean removeLine(int index) {
        return hologram().getPage(0).removeLine(0) != null;
    }

    @Override
    public int getLineCount() {
        return hologram().getPage(0).size();
    }

    @Override
    public void remove() {
        hologram().delete();
    }

    @Override
    public Iterator<HologramLine<?>> iterator() {
        return getLines().iterator();
    }

    @Override
    public String getName() {
        return hologram().getName();
    }

    @Override
    public boolean isPersistent() {
        return hologram().isSaveToFile();
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        hologram().save();
        return true;
    }

    @Override
    public void setPersistent(boolean persistent) {
        hologram().setSaveToFile(persistent);
    }

    @Override
    public Location getLocation() {
        return hologram().getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
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
        return hologram().getShowPlayers().stream()
                .map(getServer()::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public boolean addViewer(Player player) {
        if (hologram().isShowState(player)) return false;
        hologram().setShowPlayer(player);
        hologram().show(player, 0);
        return true;
    }

    @Override
    public boolean addViewers(Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return hologram().isVisible(player);
    }

    @Override
    public boolean canSee(Player player) {
        return hologram().isShowState(player) && !hologram().isHideState(player) && hologram().canShow(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return hologram().isDefaultVisibleState();
    }

    @Override
    public boolean removeViewer(Player player) {
        if (!isTrackedBy(player)) return false;
        hologram().removeShowPlayer(player);
        hologram().hide(player);
        return true;
    }

    @Override
    public boolean removeViewers(Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return hologram().getDisplayRange();
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

    @Override
    public void setDisplayRange(double range) {
        hologram().setDisplayRange((int) range);
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        hologram().setDefaultVisibleState(visible);
    }
}
