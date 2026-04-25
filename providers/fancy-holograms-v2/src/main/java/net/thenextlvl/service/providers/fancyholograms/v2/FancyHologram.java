package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.BlockHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.data.property.Visibility;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.line.BlockHologramLine;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.HologramLine;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public record FancyHologram(de.oliver.fancyholograms.api.hologram.Hologram hologram) implements Hologram {
    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        hologram().getData().setLocation(location);
        hologram().refreshForViewersInWorld();
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Stream<HologramLine> getLines() {
        return Stream.of(switch (hologram().getData().getType()) {
            case BLOCK -> new FancyBlockHologramLine(this, (BlockHologramData) hologram().getData());
            case ITEM -> new FancyItemHologramLine(this, (ItemHologramData) hologram().getData());
            case TEXT -> new FancyTextHologramLine(this, (TextHologramData) hologram().getData());
        });
    }

    @Override
    public int getLineCount() {
        return 1;
    }

    @Override
    public Optional<HologramLine> getLine(final int index) {
        if (index != 0) return Optional.empty();
        return getLines().findAny();
    }

    @Override
    public <T extends HologramLine> Optional<T> getLine(final int index, final Class<T> type) {
        return getLine(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getLineIndex(final HologramLine line) {
        return line instanceof FancyHologramLine<?> ? 0 : -1;
    }

    @Override
    public boolean removeLine(final HologramLine line) {
        return false;
    }

    @Override
    public boolean removeLine(final int index) {
        return false;
    }

    @Override
    public boolean removeLines(final Collection<HologramLine> lines) {
        return false;
    }

    @Override
    public boolean clearLines() {
        return false;
    }

    @Override
    public boolean hasLine(final HologramLine line) {
        return line instanceof final FancyHologramLine<?> fancy && equals(fancy.hologram);
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
        throw new CapabilityException("FancyHolograms does not support entity lines", HologramCapability.ENTITY_LINES);
    }

    @Override
    public EntityHologramLine addEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support entity lines", HologramCapability.ENTITY_LINES);
    }

    @Override
    public BlockHologramLine addBlockLine() throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public BlockHologramLine addBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public ItemHologramLine addItemLine() throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public ItemHologramLine addItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public TextHologramLine addTextLine() throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public TextHologramLine addTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public PagedHologramLine addPagedLine() throws CapabilityException {
        throw new CapabilityException("FancyHolograms does not support pagination", HologramCapability.PAGINATION);
    }

    @Override
    public PagedHologramLine addPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support pagination", HologramCapability.PAGINATION);
    }

    @Override
    public PagedHologramLine setPagedLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support pagination", HologramCapability.PAGINATION);
    }

    @Override
    public EntityHologramLine setEntityLine(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support entity lines", HologramCapability.ENTITY_LINES);
    }

    @Override
    public BlockHologramLine setBlockLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public ItemHologramLine setItemLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
    }

    @Override
    public TextHologramLine setTextLine(final int index) throws IndexOutOfBoundsException, CapabilityException {
        throw new CapabilityException("FancyHolograms does not support multiline holograms", HologramCapability.MULTILINE);
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
        return hologram().getViewers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull);
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::canSee)
                .map(Player::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        if (online == null) return false;
        hologram().showHologram(online);
        return canSee(online);
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean removeViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        if (online == null) return false;
        hologram().hideHologram(online);
        hologram().forceHideHologram(online);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isViewer(final UUID player) {
        final var online = Bukkit.getPlayer(player);
        return online != null && canSee(online);
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return hologram().isViewer(player);
    }

    @Override
    public boolean canSee(final Player player) {
        return hologram().meetsVisibilityConditions(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return hologram().getData().getVisibility().equals(Visibility.ALL);
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        final var current = hologram().getData().getVisibility().equals(Visibility.ALL);
        if (current == visible) return false;
        hologram().getData().setVisibility(visible ? Visibility.ALL : Visibility.MANUAL);
        return true;
    }

    @Override
    public String getName() {
        return hologram().getName();
    }

    @Override
    public boolean setName(final String name) {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return hologram().getData().isPersistent();
    }

    @Override
    public boolean setPersistent(final boolean persistent) {
        if (isPersistent() == persistent) return false;
        hologram().getData().setPersistent(persistent);
        return true;
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        FancyHologramsPlugin.get().getHologramManager().saveHolograms();
        return true;
    }

    @Override
    public Location getLocation() {
        return hologram().getData().getLocation();
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
