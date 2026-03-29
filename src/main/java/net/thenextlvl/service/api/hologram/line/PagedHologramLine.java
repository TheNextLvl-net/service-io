package net.thenextlvl.service.api.hologram.line;

import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.hologram.HologramCapability;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a hologram line that cycles through multiple pages.
 * Each page can be of any static line type (text, item, block, entity),
 * allowing for animations, GIF-like effects, and dynamic content.
 *
 * @see HologramCapability#PAGINATION
 * @since 3.0.0
 */
public interface PagedHologramLine extends HologramLine {
    /**
     * Gets all pages of this paged line.
     *
     * @return a stream of all pages
     * @since 3.0.0
     */
    Stream<StaticHologramLine> getPages();

    /**
     * Gets the page at the given index.
     *
     * @param index the page index
     * @return the page at the given index, or empty if out of bounds
     * @since 3.0.0
     */
    Optional<StaticHologramLine> getPage(int index);

    /**
     * Gets the page at the given index if it is of the given type.
     *
     * @param index the page index
     * @param type  the expected page type
     * @param <T>   the page type
     * @return the page at the given index of the given type, or empty
     * @since 3.0.0
     */
    <T extends StaticHologramLine> Optional<T> getPage(int index, Class<T> type);

    /**
     * Gets the number of pages in this paged line.
     *
     * @return the number of pages
     * @since 3.0.0
     */
    int getPageCount();

    /**
     * Gets the index of the given page.
     *
     * @param line the page to find the index of
     * @return the index of the page, or -1 if not found
     * @since 3.0.0
     */
    int getPageIndex(HologramLine line);

    /**
     * Adds a text page to this paged line.
     *
     * @return the newly created text page
     * @throws CapabilityException if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine addTextPage() throws CapabilityException;

    /**
     * Adds an item page to this paged line.
     *
     * @return the newly created item page
     * @throws CapabilityException if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine addItemPage() throws CapabilityException;

    /**
     * Adds a block page to this paged line.
     *
     * @return the newly created block page
     * @throws CapabilityException if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine addBlockPage() throws CapabilityException;

    /**
     * Adds an entity page to this paged line.
     *
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException if the entity type is not spawnable
     * @throws CapabilityException      if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine addEntityPage(EntityType entityType) throws IllegalArgumentException, CapabilityException;

    /**
     * Removes the page at the given index.
     *
     * @param index the page index
     * @return {@code true} if the page was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removePage(int index);

    /**
     * Removes the given page from this paged line.
     *
     * @param page the page to remove
     * @return {@code true} if the page was removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean removePage(HologramLine page);

    /**
     * Removes all pages from this paged line.
     *
     * @return {@code true} if any pages were removed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean clearPages();

    /**
     * Swaps the pages at the given indices.
     *
     * @param first  the index of the first page
     * @param second the index of the second page
     * @return {@code true} if the pages were swapped, {@code false} otherwise
     * @since 3.0.0
     */
    boolean swapPages(int first, int second);

    /**
     * Moves a page from one index to another.
     *
     * @param from the current index of the page
     * @param to   the target index for the page
     * @return {@code true} if the page was moved, {@code false} otherwise
     * @since 3.0.0
     */
    boolean movePage(int from, int to);

    /**
     * Sets the text page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created text page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine setTextPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets the item page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created item page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine setItemPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets the block page at the specified index.
     *
     * @param index the index at which to set the page
     * @return the newly created block page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine setBlockPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Sets the entity page at the specified index.
     *
     * @param index      the index at which to set the page
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine setEntityPage(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Inserts a text page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created text page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#TEXT_LINES} capability is not available
     * @since 3.0.0
     */
    TextHologramLine insertTextPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Inserts an item page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created item page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ITEM_LINES} capability is not available
     * @since 3.0.0
     */
    ItemHologramLine insertItemPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Inserts a block page at the specified index.
     *
     * @param index the index at which to insert the page
     * @return the newly created block page
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#BLOCK_LINES} capability is not available
     * @since 3.0.0
     */
    BlockHologramLine insertBlockPage(int index) throws IndexOutOfBoundsException, CapabilityException;

    /**
     * Inserts an entity page at the specified index.
     *
     * @param index      the index at which to insert the page
     * @param entityType the type of entity to display
     * @return the newly created entity page
     * @throws IllegalArgumentException  if the entity type is not spawnable
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws CapabilityException       if the {@link HologramCapability#ENTITY_LINES} capability is not available
     * @since 3.0.0
     */
    EntityHologramLine insertEntityPage(int index, EntityType entityType) throws IllegalArgumentException, IndexOutOfBoundsException, CapabilityException;

    /**
     * Gets the interval between page changes.
     *
     * @return the interval duration
     * @since 3.0.0
     */
    Duration getInterval();

    /**
     * Sets the interval between page changes.
     *
     * @param interval the new interval duration
     * @return this
     * @throws IllegalArgumentException if the interval is not bigger than zero
     * @since 3.0.0
     */
    PagedHologramLine setInterval(Duration interval) throws IllegalArgumentException;

    /**
     * Gets whether pages are cycled in random order.
     *
     * @return {@code true} if pages are cycled randomly, {@code false} for sequential order
     * @since 3.0.0
     */
    boolean isRandomOrder();

    /**
     * Sets whether pages should be cycled in random order.
     *
     * @param random {@code true} for random order, {@code false} for sequential
     * @return this
     * @since 3.0.0
     */
    PagedHologramLine setRandomOrder(boolean random);

    /**
     * Gets whether the paged line is currently paused.
     *
     * @return {@code true} if paused, {@code false} otherwise
     * @since 3.0.0
     */
    boolean isPaused();

    /**
     * Sets whether the paged line should be paused.
     * When paused, the line will not automatically cycle through pages.
     *
     * @param paused {@code true} to pause, {@code false} to resume
     * @return this
     * @since 3.0.0
     */
    PagedHologramLine setPaused(boolean paused);

    /**
     * Cycles the page of this line for the given player.
     * <p>
     * This is equivalent to calling {@link #cyclePage(Player, int)} with an amount of one.
     *
     * @param player the player
     * @return a future that completes when the page has been cycled
     * @see #cyclePage(Player, int)
     * @since 3.0.0
     */
    CompletableFuture<Boolean> cyclePage(Player player);

    /**
     * Cycles the page of this line for the given player by the given amount.
     * <p>
     * Opposed to {@link #setPage(Player, int)}, this method will not throw an exception
     * if the page index is out of bounds and will instead wrap around.
     *
     * @param player the player
     * @param amount the amount to cycle by; negative amounts cycle backwards
     * @return a future that completes when the page has been cycled
     * @since 3.0.0
     */
    CompletableFuture<Boolean> cyclePage(Player player, int amount);

    /**
     * Sets the page of this line for the given player.
     *
     * @param player the player
     * @param page   the page index
     * @return a future that completes when the page has been set
     * @throws IndexOutOfBoundsException if the page index is out of bounds
     * @see #getPageCount()
     * @since 3.0.0
     */
    CompletableFuture<Boolean> setPage(Player player, int page) throws IndexOutOfBoundsException;

    /**
     * Gets the current page index of this line for the given player.
     *
     * @param player the player
     * @return the current page index, or empty if not set
     * @since 3.0.0
     */
    OptionalInt getCurrentPageIndex(Player player);

    /**
     * Gets the current page of this line for the given player.
     *
     * @param player the player
     * @return the current page, or empty if not set
     * @since 3.0.0
     */
    Optional<StaticHologramLine> getCurrentPage(Player player);

    /**
     * Iterates over all pages of this line.
     *
     * @param action the action consumer
     * @since 3.0.0
     */
    void forEachPage(Consumer<StaticHologramLine> action);
}
