package net.thenextlvl.service.api.model;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

@NullMarked
public interface Viewable {
    Location getLocation(); // copy

    Server getServer();

    @Unmodifiable
    Set<Player> getTrackedBy();

    @Unmodifiable
    Set<Player> getViewers();

    World getWorld();

    boolean addViewer(Player player);

    boolean addViewers(Collection<Player> player);

    boolean isInvisible();

    boolean isTrackedBy(Player player);

    boolean isViewer(Player player);

    boolean isVisibleByDefault();

    boolean removeViewer(Player player);

    boolean removeViewers(Collection<Player> player);

    double getDisplayRange();

    double getX();

    double getY();

    double getZ();

    float getPitch();

    float getYaw();

    void setDisplayRange(double range);

    void setInvisible(boolean invisible);

    void setVisibleByDefault(boolean visible);
}
