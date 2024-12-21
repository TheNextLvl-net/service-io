package net.thenextlvl.service.api.npc;

import net.kyori.adventure.text.Component;
import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@NullMarked
public interface Character<T extends Entity> extends Persistable, Viewable {
    CompletableFuture<Boolean> teleportAsync(Location location);

    Component getDisplayName();

    Optional<T> getEntity();

    EntityType getType();

    @Override
    @Nullable
    Location getLocation();

    @Override
    @Nullable
    World getWorld();

    boolean isProtected();

    boolean isSpawned();

    boolean isTablistEntryHidden();

    boolean remove();

    boolean respawn();

    boolean spawn(@NonNull Location location);

    void delete();

    void setDisplayName(Component displayName);

    void setProtected(boolean protect);

    void setTablistEntryHidden(boolean hidden);
}
