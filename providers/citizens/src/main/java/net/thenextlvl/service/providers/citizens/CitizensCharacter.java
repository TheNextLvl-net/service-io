package net.thenextlvl.service.providers.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.service.api.character.Character;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    public boolean isInvulnerable() {
        return npc.isProtected();
    }

    @Override
    public void remove() {
        npc.destroy();
    }

    @Override
    public void setCollidable(boolean collidable) {
        npc.data().setPersistent(NPC.Metadata.COLLIDABLE, collidable);
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
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        return getEntity().map(entity -> entity.teleportAsync(location)).orElseGet(() -> {
            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            return CompletableFuture.completedFuture(true);
        });
    }

    @Override
    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(npc.getRawName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> getEntity() {
        return Optional.ofNullable(npc.getEntity())
                .map(entity -> (T) entity);
    }

    @Override
    public EntityType getType() {
        return npc.getTraitOptional(MobType.class)
                .transform(MobType::getType)
                .or(EntityType.PLAYER);
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
    public boolean spawn(Location location) {
        return npc.spawn(location);
    }

    @Override
    public void lookAt(Entity entity) {
        lookAt(entity.getLocation());
    }

    @Override
    public void lookAt(Location location) {
        npc.faceLocation(location);
    }

    @Override
    public boolean despawn() {
        return npc.despawn();
    }

    @Override
    public boolean isCollidable() {
        return npc.data().get(NPC.Metadata.COLLIDABLE, false);
    }

    @Override
    public boolean respawn() {
        var location = getLocation();
        return location != null && npc.despawn(DespawnReason.PENDING_RESPAWN) && npc.spawn(location);
    }

    @Override
    public void setDisplayName(Component displayName) {
        npc.setName(MiniMessage.miniMessage().serialize(displayName));
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        npc.setProtected(invulnerable);
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
