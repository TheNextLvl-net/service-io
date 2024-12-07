package net.thenextlvl.service.api.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

@NullMarked
public interface Viewable {
    // add the given player to the viewers of this object
    boolean addViewer(Player player);

    // add all given players to the viewers of this object
    boolean addViewers(Collection<Player> player);

    // remove this object for the given player
    boolean removeViewer(Player player);

    // remove this object for all given players
    boolean removeViewers(Collection<Player> player);

    // all players that can actively see this object
    @Unmodifiable
    Set<Player> getTrackedBy();

    // all players this object should be sent to
    @Unmodifiable
    Set<Player> getViewers();

    // whether the object should be sent to the player in the first place
    boolean isViewer(Player player);

    // whether the object is currently visible to the player
    boolean isTrackedBy(Player player);
}
