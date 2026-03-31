package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramPage;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.LineType;
import net.thenextlvl.service.api.hologram.line.BlockHologramLine;
import net.thenextlvl.service.api.hologram.line.EntityHologramLine;
import net.thenextlvl.service.api.hologram.line.HologramLine;
import net.thenextlvl.service.api.hologram.line.ItemHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import net.thenextlvl.service.api.hologram.line.StaticHologramLine;
import net.thenextlvl.service.api.hologram.line.TextHologramLine;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public class DecentPagedHologramLine implements PagedHologramLine {
    protected final DecentHologram hologram;
    protected final HologramPage page;

    protected DecentPagedHologramLine(final DecentHologram hologram, final HologramPage page) {
        this.hologram = hologram;
        this.page = page;
    }

    @Override
    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public LineType getType() {
        return LineType.PAGED;
    }

    @Override
    public World getWorld() {
        return hologram.getWorld();
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
    public boolean canSee(final Player player) {
        return hologram.canSee(player);
    }

    @Override
    public Stream<StaticHologramLine> getPages() {
        return page.getLines().stream().map(this::getLine);
    }

    @Override
    public Optional<StaticHologramLine> getPage(final int index) {
        final var lines = page.getLines();
        if (index < 0 || index >= lines.size()) return Optional.empty();
        return Optional.of(lines.get(index)).map(this::getLine);
    }

    private StaticHologramLine getLine(final eu.decentsoftware.holograms.api.holograms.HologramLine line) {
        return switch (line.getType()) {
            case TEXT -> new DecentTextHologramLine(hologram, line);
            case HEAD -> new DecentBlockHologramLine(hologram, line, false);
            case SMALLHEAD -> new DecentBlockHologramLine(hologram, line, true);
            case ENTITY -> new DecentEntityHologramLine(hologram, line);
            case ICON -> new DecentItemHologramLine(hologram, line);
            default -> throw new IllegalStateException("Unknown line type: " + line);
        };
    }

    @Override
    public <T extends StaticHologramLine> Optional<T> getPage(final int index, final Class<T> type) {
        return getPage(index).filter(type::isInstance).map(type::cast);
    }

    @Override
    public int getPageCount() {
        return page.getLines().size();
    }

    @Override
    public int getPageIndex(final HologramLine line) {
        if (!(line instanceof final DecentHologramLine decent)) return -1;
        final var parent = decent.line.getParent();
        return parent != null ? hologram.hologram().getPages().indexOf(parent) : -1;
    }

    @Override
    public TextHologramLine addTextPage() throws CapabilityException {
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), ""
        );
        page.addLine(line);
        return new DecentTextHologramLine(hologram, line);
    }

    @Override
    public ItemHologramLine addItemPage() throws CapabilityException {
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ICON:STONE"
        );
        page.addLine(line);
        return new DecentItemHologramLine(hologram, line);
    }

    @Override
    public BlockHologramLine addBlockPage() throws CapabilityException {
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#HEAD:STONE"
        );
        page.addLine(line);
        return new DecentBlockHologramLine(hologram, line, false);
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ENTITY:" + entityType.name()
        );
        page.addLine(line);
        return new DecentEntityHologramLine(hologram, line);
    }

    @Override
    public boolean removePage(final int index) {
        return page.removeLine(index) != null;
    }

    @Override
    public boolean removePage(final HologramLine line) {
        final var index = getPageIndex(line);
        return index >= 0 && removePage(index);
    }

    @Override
    public boolean clearPages() {
        if (page.getLines().isEmpty()) return false;
        while (!page.getLines().isEmpty()) {
            page.removeLine(0);
        }
        return true;
    }

    @Override
    public boolean swapPages(final int first, final int second) {
        return hologram.hologram().swapPages(first, second);
    }

    @Override
    public boolean movePage(final int from, final int to) {
        final var pages = hologram.hologram().getPages();
        if (from < 0 || from >= pages.size() || to < 0 || to >= pages.size() || from == to) return false;
        final var page = pages.remove(from);
        pages.add(to, page);
        return true;
    }

    @Override
    public TextHologramLine setTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), ""
        );
        page.addLine(line);
        return new DecentTextHologramLine(hologram, line);
    }

    @Override
    public ItemHologramLine setItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ICON:STONE"
        );
        page.addLine(line);
        return new DecentItemHologramLine(hologram, line);
    }

    @Override
    public BlockHologramLine setBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#HEAD:STONE"
        );
        page.addLine(line);
        return new DecentBlockHologramLine(hologram, line, false);
    }

    @Override
    public EntityHologramLine setEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        Objects.checkIndex(index, page.getLines().size());
        page.removeLine(index);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ENTITY:" + entityType.name()
        );
        page.addLine(line);
        return new DecentEntityHologramLine(hologram, line);
    }

    @Override
    public TextHologramLine insertTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), ""
        );
        page.addLine(line);
        return new DecentTextHologramLine(hologram, line);
    }

    @Override
    public ItemHologramLine insertItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ICON:STONE"
        );
        page.addLine(line);
        return new DecentItemHologramLine(hologram, line);
    }

    @Override
    public BlockHologramLine insertBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        Objects.checkIndex(index, page.getLines().size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#HEAD:STONE"
        );
        page.addLine(line);
        return new DecentBlockHologramLine(hologram, line, false);
    }

    @Override
    public EntityHologramLine insertEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        if (!entityType.isSpawnable()) throw new IllegalArgumentException("Invalid entity type: " + entityType.name());
        Objects.checkIndex(index, page.getLines().size() + 1);
        final var line = new eu.decentsoftware.holograms.api.holograms.HologramLine(
                page, hologram.getLocation(), "#ENTITY:" + entityType.name()
        );
        page.addLine(line);
        return new DecentEntityHologramLine(hologram, line);
    }

    @Override
    public Duration getInterval() {
        return Duration.ofSeconds(5);
    }

    @Override
    public boolean setInterval(final Duration interval) throws IllegalArgumentException {
        if (!interval.isPositive()) throw new IllegalArgumentException("Interval must be positive");
        return false;
    }

    @Override
    public boolean isRandomOrder() {
        return false;
    }

    @Override
    public boolean setRandomOrder(final boolean random) {
        return false;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public boolean setPaused(final boolean paused) {
        return false;
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player, final int amount) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> setPage(final Player player, final int page) throws IndexOutOfBoundsException {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public OptionalInt getCurrentPageIndex(final Player player) {
        return OptionalInt.empty();
    }

    @Override
    public Optional<StaticHologramLine> getCurrentPage(final Player player) {
        return Optional.empty();
    }

    @Override
    public void forEachPage(final Consumer<StaticHologramLine> action) {
        getPages().forEach(action);
    }
}
