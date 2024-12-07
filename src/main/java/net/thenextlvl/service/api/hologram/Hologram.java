package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An interface that represents a hologram
 */
@NullMarked
public interface Hologram extends Viewable, Iterable<HologramLine<?>> {
    CompletableFuture<Boolean> teleport(Location location);

    CompletableFuture<Boolean> teleport(Location location, PlayerTeleportEvent.TeleportCause cause);

    @Unmodifiable
    List<HologramLine<?>> getLines();

    Location getLocation();

    Server getServer();

    World getWorld();

    boolean addLine(HologramLine<?> line);

    boolean addLine(int index, HologramLine<?> line);

    boolean isGlowing();

    int getLineCount();

    void remove();

    void setGlowing(boolean glowing);
}