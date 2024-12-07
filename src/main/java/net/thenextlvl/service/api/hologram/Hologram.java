package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An interface that represents a hologram
 */
@NullMarked
public interface Hologram extends Persistable, Viewable, Iterable<HologramLine<?>> {
    CompletableFuture<Boolean> teleportAsync(Location location);

    CompletableFuture<Boolean> teleportAsync(Location location, PlayerTeleportEvent.TeleportCause cause);

    @Unmodifiable
    List<HologramLine<?>> getLines();

    boolean addLine(HologramLine<?> line);

    boolean addLine(int index, HologramLine<?> line);


    int getLineCount();

    void remove();
}
