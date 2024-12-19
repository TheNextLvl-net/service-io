package net.thenextlvl.service.api.npc;

import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public interface Character<T extends Entity> extends Persistable, Viewable {
    Optional<T> getEntity();

    @Override
    @Nullable
    Location getLocation();

    @Override
    @Nullable
    World getWorld();

    boolean isProtected();

    boolean isSpawned();

    boolean isTablistEntryHidden();

    boolean spawn();

    boolean spawn(@NonNull Location location);

    void remove();

    void setProtected(boolean protect);

    void setTablistEntryHidden(boolean hidden);
}
