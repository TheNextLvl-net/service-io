package net.thenextlvl.service.providers.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.service.character.Character;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
public final class CitizensCharacter implements Character {
    private final NPC npc;

    public CitizensCharacter(final NPC npc) {
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
    public boolean setPersistent(final boolean persistent) {
        if (isPersistent() == persistent) return false;
        npc.data().setPersistent(NPC.Metadata.SHOULD_SAVE, persistent);
        return true;
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
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet())
                ).orElseGet(Set::of);
    }

    @Override
    public boolean addViewer(final Player player) {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .filter(filter -> filter.isHidden(player))
                .map(filter -> {
                    filter.addPlayer(player.getUniqueId());
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean addViewers(final Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return getEntity().map(entity -> entity.getTrackedBy().contains(player)).orElse(false);
    }

    @Override
    public boolean canSee(final Player player) {
        return !npc.isHiddenFrom(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return getEntity().map(Entity::isVisibleByDefault).orElse(false);
    }

    @Override
    public boolean removeViewer(final Player player) {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .filter(filter -> !filter.isHidden(player))
                .map(filter -> {
                    filter.removePlayer(player.getUniqueId());
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean removeViewers(final Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .map(PlayerFilter::getApplyRange)
                .orElse(-1d);
    }

    @Override
    public void setDisplayRange(final double range) {
        npc.getTraitOptional(PlayerFilter.class).toJavaUtil()
                .ifPresent(filter -> filter.setApplyRange(range));
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        return getEntity().map(entity -> {
            if (entity.isVisibleByDefault() == visible) return false;
            entity.setVisibleByDefault(visible);
            return true;
        }).orElse(false);
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.ofNullable(npc.getStoredLocation());
    }

    @Override
    public Optional<World> getWorld() {
        return getEntity().map(Entity::getWorld);
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
    public void setCollidable(final boolean collidable) {
        npc.data().setPersistent(NPC.Metadata.COLLIDABLE, collidable);
    }

    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
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
    public Optional<Entity> getEntity() {
        return Optional.ofNullable(npc.getEntity());
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
    public boolean spawn(final Location location) {
        return npc.spawn(location);
    }

    @Override
    public void lookAt(final Entity entity) {
        lookAt(entity.getLocation());
    }

    @Override
    public void lookAt(final Location location) {
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
        return getLocation().map(location -> npc.despawn(DespawnReason.PENDING_RESPAWN) && npc.spawn(location)).orElse(false);
    }

    @Override
    public void setDisplayName(final Component displayName) {
        npc.setName(MiniMessage.miniMessage().serialize(displayName));
    }

    @Override
    public void setInvulnerable(final boolean invulnerable) {
        npc.setProtected(invulnerable);
    }

    @Override
    public void setTablistEntryHidden(final boolean hidden) {
        npc.data().setPersistent(NPC.Metadata.REMOVE_FROM_TABLIST, hidden);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final CitizensCharacter that = (CitizensCharacter) o;
        return Objects.equals(npc, that.npc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(npc);
    }
}
