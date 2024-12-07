package net.thenextlvl.service.api.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

@NullMarked
public interface Viewable extends Positioned {
    @Unmodifiable
    Set<Player> getTrackedBy();

    @Unmodifiable
    Set<Player> getViewers();

    boolean addViewer(Player player);

    boolean addViewers(Collection<Player> players);

    boolean isInvisible();

    boolean isTrackedBy(Player player);

    boolean isViewer(Player player);

    boolean isVisibleByDefault();

    boolean removeViewer(Player player);

    boolean removeViewers(Collection<Player> players);

    double getDisplayRange();

    void setDisplayRange(double range);

    void setInvisible(boolean invisible);

    void setVisibleByDefault(boolean visible);
}
