package net.thenextlvl.service.api.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

/**
 * The Viewable interface represents an object that can be viewed or tracked by players within a specific range.
 * It provides methods to manage visibility, track players, and modify how the object is displayed.
 */
@NullMarked
public interface Viewable extends Positioned {
    /**
     * Retrieves an unmodifiable set of players currently tracking this viewable object.
     * The returned set represents all players for whom this object is actively being tracked.
     *
     * @return an unmodifiable set of players tracking this viewable object
     */
    @Unmodifiable
    Set<Player> getTrackedBy();

    /**
     * Retrieves an unmodifiable set of players that may be able to view this object.
     * The returned set includes all players that meet the required conditions to view this object.
     *
     * @return an unmodifiable set of players that may view this object
     */
    @Unmodifiable
    Set<Player> getViewers();

    /**
     * Adds a player to the list of viewers of this viewable object.
     *
     * @param player The player to be added as a viewer.
     * @return whether the player was successfully added as a viewer.
     */
    boolean addViewer(Player player);

    /**
     * Adds multiple players as viewers to this viewable object.
     *
     * @param players A collection of players to be added as viewers.
     * @return true if at least one player was successfully added as a viewer, false otherwise.
     */
    boolean addViewers(Collection<Player> players);

    /**
     * Determines whether the specified player is currently tracking this viewable object.
     *
     * @param player The player to check for tracking status.
     * @return true if the specified player is actively tracking this object; false otherwise.
     */
    boolean isTrackedBy(Player player);

    // whether the player meets all conditions to view this object

    /**
     * Determines whether the specified player can see this viewable object.
     *
     * @param player The player to check for visibility conditions.
     * @return true if the specified player meets all conditions to view this object; false otherwise.
     */
    boolean canSee(Player player);

    /**
     * Determines if the viewable object is visible by default.
     *
     * @return true if the object is visible by default; false otherwise.
     */
    boolean isVisibleByDefault();

    /**
     * Removes a player from the list of viewers for this viewable object.
     *
     * @param player The player to be removed from the viewers list.
     * @return true if the player was successfully removed from the viewer list; false otherwise.
     */
    boolean removeViewer(Player player);

    /**
     * Removes a collection of players from the list of viewers for this viewable object.
     *
     * @param players A collection of players to be removed as viewers.
     * @return true if at least one player was successfully removed from the viewer list, false otherwise.
     */
    boolean removeViewers(Collection<Player> players);

    /**
     * Retrieves the display range for this viewable object.
     *
     * @return the display range as a double value.
     */
    double getDisplayRange();

    /**
     * Sets the display range for this viewable object.
     *
     * @param range The new display range to set, represented as a double value.
     */
    void setDisplayRange(double range);

    /**
     * Sets the default visibility state for this viewable object.
     *
     * @param visible whether the object should be visible by default.
     */
    void setVisibleByDefault(boolean visible);
}
