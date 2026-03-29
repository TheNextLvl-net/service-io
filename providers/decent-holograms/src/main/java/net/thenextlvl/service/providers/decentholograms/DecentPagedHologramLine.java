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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

@NullMarked
public class DecentPagedHologramLine implements PagedHologramLine {
    protected final HologramPage page;
    protected final Hologram hologram;

    protected DecentPagedHologramLine(final Hologram hologram, final HologramPage page) {
        this.hologram = hologram;
        this.page = page;
    }

    @Override
    public Stream<StaticHologramLine> getPages() {
        return page.getLines().stream().map(line -> switch (line.getType()) {
            case TEXT -> new DecentTextHologramLine(line);
            case HEAD -> new DecentBlockHologramLine(hologram, line, false);
            case SMALLHEAD -> new DecentBlockHologramLine(hologram, line, true);
            case ENTITY -> new DecentEntityHologramLine(line);
            case ICON -> new DecentItemHologramLine(line);
            default -> throw new IllegalStateException("Unknown line type: " + line);
        });
    }

    @Override
    public Optional<StaticHologramLine> getPage(final int index) {
        return Optional.empty();
    }

    @Override
    public <T extends StaticHologramLine> Optional<T> getPage(final int index, final Class<T> type) {
        return Optional.empty();
    }

    @Override
    public int getPageCount() {
        return 0;
    }

    @Override
    public int getPageIndex(final HologramLine line) {
        return 0;
    }

    @Override
    public TextHologramLine addTextPage() throws CapabilityException {
        return null;
    }

    @Override
    public ItemHologramLine addItemPage() throws CapabilityException {
        return null;
    }

    @Override
    public BlockHologramLine addBlockPage() throws CapabilityException {
        return null;
    }

    @Override
    public EntityHologramLine addEntityPage(final EntityType entityType) throws IllegalArgumentException, CapabilityException {
        return null;
    }

    @Override
    public boolean removePage(final int index) {
        return false;
    }

    @Override
    public boolean removePage(final HologramLine page) {
        return false;
    }

    @Override
    public boolean clearPages() {
        return false;
    }

    @Override
    public boolean swapPages(final int first, final int second) {
        return false;
    }

    @Override
    public boolean movePage(final int from, final int to) {
        return false;
    }

    @Override
    public TextHologramLine setTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public ItemHologramLine setItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public BlockHologramLine setBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public EntityHologramLine setEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public TextHologramLine insertTextPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public ItemHologramLine insertItemPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public BlockHologramLine insertBlockPage(final int index) throws IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public EntityHologramLine insertEntityPage(final int index, final EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException {
        return null;
    }

    @Override
    public Duration getInterval() {
        return null;
    }

    @Override
    public PagedHologramLine setInterval(final Duration interval) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean isRandomOrder() {
        return false;
    }

    @Override
    public PagedHologramLine setRandomOrder(final boolean random) {
        return null;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public PagedHologramLine setPaused(final boolean paused) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> cyclePage(final Player player, final int amount) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> setPage(final Player player, final int page) throws IndexOutOfBoundsException {
        return null;
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

    }

    @Override
    public Hologram getHologram() {
        return null;
    }

    @Override
    public LineType getType() {
        return null;
    }

    @Override
    public World getWorld() {
        return null;
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
        return false;
    }
}
