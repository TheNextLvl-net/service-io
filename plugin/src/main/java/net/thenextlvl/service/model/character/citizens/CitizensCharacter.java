package net.thenextlvl.service.model.character.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.thenextlvl.service.api.npc.Character;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public class CitizensCharacter<T extends Entity> implements Character<T> {
    protected final NPC npc;

    public CitizensCharacter(NPC npc) {
        this.npc = npc;
    }

    @Override
    public String getName() {
        return npc.getRawName();
    }

    @Override
    public boolean isPersistent() {
        return npc.data().get(NPC.Metadata.SHOULD_SAVE);
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        CitizensAPI.getNPCRegistry().saveToStore();
        return true;
    }

    @Override
    public void setPersistent(boolean persistent) {
        npc.data().setPersistent(NPC.Metadata.SHOULD_SAVE, persistent);
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return getEntity()
                .map(Entity::getTrackedBy)
                .orElseGet(Set::of);
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .map(PlayerFilter::getPlayerUUIDs)
                .map(players -> players.stream()
                        .map(getServer()::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet())
                ).orElseGet(Set::of);
    }

    @Override
    public boolean addViewer(Player player) {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .filter(filter -> filter.isHidden(player))
                .map(filter -> {
                    filter.addPlayer(player.getUniqueId());
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean addViewers(Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return getEntity().map(entity -> entity.getTrackedBy().contains(player)).orElse(false);
    }

    @Override
    public boolean canSee(Player player) {
        return !npc.isHiddenFrom(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return getEntity().map(Entity::isVisibleByDefault).orElse(false);
    }

    @Override
    public boolean removeViewer(Player player) {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .filter(filter -> !filter.isHidden(player))
                .map(filter -> {
                    filter.removePlayer(player.getUniqueId());
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean removeViewers(Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .map(PlayerFilter::getApplyRange)
                .orElse(-1d);
    }

    @Override
    public void setDisplayRange(double range) {
        npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .ifPresent(filter -> filter.setApplyRange(range));
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        getEntity().ifPresent(entity -> entity.setVisibleByDefault(visible));
    }

    @Override
    public @Nullable Location getLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public @Nullable World getWorld() {
        return getEntity().map(Entity::getWorld).orElse(null);
    }

    @Override
    public double getX() {
        return getEntity().map(Entity::getX).orElse(0d);
    }

    @Override
    public double getY() {
        return getEntity().map(Entity::getY).orElse(0d);
    }

    @Override
    public double getZ() {
        return getEntity().map(Entity::getZ).orElse(0d);
    }

    @Override
    public float getPitch() {
        return getEntity().map(Entity::getPitch).orElse(0f);
    }

    @Override
    public float getYaw() {
        return getEntity().map(Entity::getYaw).orElse(0f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> getEntity() {
        return Optional.ofNullable(npc.getEntity())
                .map(entity -> (T) entity);
    }

    @Override
    public boolean isProtected() {
        return npc.isProtected();
    }

    @Override
    public boolean isSpawned() {
        return npc.isSpawned();
    }

    @Override
    public boolean isTablistEntryHidden() {
        return npc.shouldRemoveFromTabList();
    }

    @Override
    public boolean spawn() {
        var location = getLocation();
        return location != null && spawn(location);
    }

    @Override
    public boolean spawn(Location location) {
        return npc.spawn(location);
    }

    @Override
    public void remove() {
        npc.destroy();
    }

    @Override
    public void setProtected(boolean protect) {
        npc.setProtected(protect);
    }

    @Override
    public void setTablistEntryHidden(boolean hidden) {
        npc.data().setPersistent(NPC.Metadata.REMOVE_FROM_TABLIST, hidden);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CitizensCharacter<?> that = (CitizensCharacter<?>) o;
        return Objects.equals(npc, that.npc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(npc);
    }
}
