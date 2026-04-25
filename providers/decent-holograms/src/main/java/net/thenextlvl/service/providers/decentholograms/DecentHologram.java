package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.line.BlockHologramLine;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.HologramLine;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@NullMarked
public record DecentHologram(eu.decentsoftware.holograms.api.holograms.Hologram hologram) implements Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        DHAPI.moveHologram(hologram, location);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Stream<HologramLine> getLines() {
        return hologram.getPages().stream().flatMap(page -> page.getLines().stream().map(this::getLine));
    }

    @Override
    public int getLineCount() {
        return hologram.getPage(0).size();
    }

    @Override
    public Optional<HologramLine> getLine(final int index) {
        final var lines = hologram.getPage(0).getLines();
        if (index < 0 || index >= lines.size()) return Optional.empty();
        final var line = lines.get(index);
        return Optional.of(getLine(line));
    }

    private HologramLine getLine(final eu.decentsoftware.holograms.api.holograms.HologramLine line) {
        return switch (line.getType()) {
            case TEXT -> new DecentTextHologramLine(this, line);
            case HEAD -> new DecentBlockHologramLine(this, line, false);
            case SMALLHEAD -> new DecentBlockHologramLine(this, line, true);
            case ENTITY -> new DecentEntityHologramLine(this, line);
            case ICON -> new DecentItemHologramLine(this, line);
            default -> throw new IllegalStateException("Unknown line type: " + line);
        };
    }

    @Override
    public <T extends HologramLine> Optional<T> getLine(final int index, final Class<T> type) {
        return getLine(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getLineIndex(final HologramLine line) {
        if (!(line instanceof final DecentHologramLine decent)) return -1;
        final var parent = decent.line.getParent();
        return parent != null ? parent.getLines().indexOf(decent.line) : -1;
    }

    @Override
    public boolean removeLine(final HologramLine line) {
        if (!(line instanceof final DecentHologramLine decent)) return false;
        final var parent = decent.line.getParent();
        return parent != null && parent.removeLine(getLineIndex(decent)) != null;
    }

    @Override
    public boolean removeLine(final int index) {
        return hologram.getPage(0).removeLine(index) != null;
    }

    @Override
    public boolean removeLines(final Collection<HologramLine> lines) {
        return lines.stream().map(this::removeLine).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean clearLines() {
        if (hologram.getPages().isEmpty()) return false;
        while (!hologram.getPages().isEmpty()) {
            hologram.removePage(0);
        }
        return true;
    }

    @Override
    public boolean hasLine(final HologramLine line) {
        if (!(line instanceof final DecentHologramLine decent)) return false;
        for (final var page : hologram.getPages()) {
            if (page.getLines().contains(decent.line)) return true;
        }
        return false;
    }

    @Override
    public boolean moveLine(final int from, final int to) {
        return false;
    }

    @Override
    public boolean swapLines(final int line1, final int line2) {
        return false;
    }

    @Override
    public EntityHologramLine addEntityLine(final EntityType entityType) throws IllegalArgumentException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        final var page = hologram.getPage(0);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ENTITY:" + entityType.name());
        page.addLine(line);
        return new DecentEntityHologramLine(this, line);
    }

    @Override
    public EntityHologramLine addEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ENTITY:" + entityType.name());
        page.addLine(line);
        return new DecentEntityHologramLine(this, line);
    }

    @Override
    public BlockHologramLine addBlockLine() throws CapabilityException {
        final var page = hologram.getPage(0);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#HEAD:STONE");
        page.addLine(line);
        return new DecentBlockHologramLine(this, line, false);
    }

    @Override
    public BlockHologramLine addBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#HEAD:STONE");
        page.addLine(line);
        return new DecentBlockHologramLine(this, line, false);
    }

    @Override
    public ItemHologramLine addItemLine() throws CapabilityException {
        final var page = hologram.getPage(0);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ICON:STONE");
        page.addLine(line);
        return new DecentItemHologramLine(this, line);
    }

    @Override
    public ItemHologramLine addItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ICON:STONE");
        page.addLine(line);
        return new DecentItemHologramLine(this, line);
    }

    @Override
    public TextHologramLine addTextLine() throws CapabilityException {
        final var page = hologram.getPage(0);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "");
        page.addLine(line);
        return new DecentTextHologramLine(this, line);
    }

    @Override
    public TextHologramLine addTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "");
        page.addLine(line);
        return new DecentTextHologramLine(this, line);
    }

    @Override
    public PagedHologramLine addPagedLine() throws CapabilityException {
        hologram.addPage();
        final var pageIndex = hologram.size() - 1;
        return new DecentPagedHologramLine(this, hologram.getPage(pageIndex));
    }

    @Override
    public PagedHologramLine addPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        hologram.addPage();
        final var pageIndex = hologram.size() - 1;
        return new DecentPagedHologramLine(this, hologram.getPage(pageIndex));
    }

    @Override
    public PagedHologramLine setPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, hologram.size());
        return new DecentPagedHologramLine(this, hologram.getPage(index));
    }

    @Override
    public EntityHologramLine setEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ENTITY:" + entityType.name());
        page.addLine(line);
        return new DecentEntityHologramLine(this, line);
    }

    @Override
    public BlockHologramLine setBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#HEAD:STONE");
        page.addLine(line);
        return new DecentBlockHologramLine(this, line, false);
    }

    @Override
    public ItemHologramLine setItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "#ICON:STONE");
        page.addLine(line);
        return new DecentItemHologramLine(this, line);
    }

    @Override
    public TextHologramLine setTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        final var page = hologram.getPage(0);
        Objects.checkIndex(index, page.size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(page, getLocation(), "");
        page.addLine(line);
        return new DecentTextHologramLine(this, line);
    }

    @Override
    public Optional<String> getViewPermission() {
        return Optional.empty();
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        return false;
    }

    @Override
    public Stream<Player> getTrackedBy() {
        return hologram.getViewers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull);
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(hologram.getShowPlayers());
    }

    @Override
    public boolean addViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        if (online == null || hologram.isShowState(online)) return false;
        hologram.setShowPlayer(online);
        return true;
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        if (online == null || !hologram.isShowState(online)) return false;
        hologram.removeShowPlayer(online);
        hologram.update(true, online);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        return online != null && hologram.isShowState(online);
    }

    @Override
    public boolean canSee(final Player player) {
        return hologram.isShowState(player) && !hologram.isHideState(player) && hologram.canShow(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return hologram.isDefaultVisibleState();
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        if (hologram.isDefaultVisibleState() == visible) return false;
        hologram.setDefaultVisibleState(visible);
        return true;
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return hologram.isVisible(player);
    }

    @Override
    public String getName() {
        return hologram.getName();
    }

    @Override
    public boolean setName(final String name) {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return hologram.isSaveToFile();
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        if (isPersistent() == persistent) return false;
        hologram.setSaveToFile(persistent);
        return true;
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        hologram.save();
        return true;
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public Iterator<HologramLine> iterator() {
        return getLines().iterator();
    }
}
