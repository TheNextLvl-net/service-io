package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

/**
 * An interface that represents a hologram
 */
@NullMarked
public interface Hologram extends Entity, Viewable, Iterable<HologramLine> {
    List<HologramLine> getLines();

    Optional<HologramLine> getLine(int index);

    boolean addLine(HologramLine line);

    boolean addLine(int index, HologramLine line);

    int getLineCount();
}
