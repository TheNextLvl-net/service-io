package net.thenextlvl.service.model.character.fancy;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
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
public record FancyCharacter<T extends Entity>(Npc npc) implements Character<T> {
    @Override
    public CompletableFuture<Boolean> teleportAsync(Location location) {
        npc().getData().setLocation(location);
        npc().moveForAll(false);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(npc.getData().getDisplayName());
    }

    @Override
    public Optional<T> getEntity() {
        return Optional.empty();
    }

    @Override
    public EntityType getType() {
        return npc().getData().getType();
    }

    @Override
    public @Nullable Location getLocation() {
        return npc().getData().getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public @Nullable World getWorld() {
        var location = getLocation();
        return location != null ? location.getWorld() : null;
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public void remove() {
        npc().removeForAll();
        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc());
    }

    @Override
    public void setCollidable(boolean collidable) {
        npc().getData().setCollidable(collidable);
    }

    @Override
    public double getX() {
        var location = getLocation();
        return location != null ? location.getX() : 0;
    }

    @Override
    public double getY() {
        var location = getLocation();
        return location != null ? location.getY() : 0;
    }

    @Override
    public double getZ() {
        var location = getLocation();
        return location != null ? location.getZ() : 0;
    }

    @Override
    public float getPitch() {
        var location = getLocation();
        return location != null ? location.getPitch() : 0;
    }

    @Override
    public float getYaw() {
        var location = getLocation();
        return location != null ? location.getYaw() : 0;
    }

    @Override
    public boolean isSpawned() {
        return true;
    }

    @Override
    public boolean isTablistEntryHidden() {
        return !npc().getData().isShowInTab();
    }

    @Override
    public boolean spawn(Location location) {
        npc().getData().setLocation(location);
        npc().getData().setSpawnEntity(true);
        npc().spawnForAll();
        return true;
    }

    @Override
    public void lookAt(Entity entity) {
        lookAt(entity.getLocation());
    }

    @Override
    public void lookAt(Location target) {
        var location = npc().getData().getLocation().clone();
        var direction = location.setDirection(target.clone().subtract(location).toVector());
        teleportAsync(direction);
    }

    @Override
    public boolean despawn() {
        npc().getData().setSpawnEntity(false);
        npc().removeForAll();
        return true;
    }

    @Override
    public boolean isCollidable() {
        return npc().getData().isCollidable();
    }

    @Override
    public boolean respawn() {
        npc().removeForAll();
        npc().spawnForAll();
        return true;
    }

    @Override
    public void setDisplayName(Component displayName) {
        npc().getData().setDisplayName(MiniMessage.miniMessage().serialize(displayName));
        npc().updateForAll();
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
    }

    @Override
    public void setTablistEntryHidden(boolean hidden) {
        npc().getData().setShowInTab(!hidden);
    }

    @Override
    public String getName() {
        return npc().getData().getName();
    }

    @Override
    public boolean isPersistent() {
        return npc().isSaveToFile();
    }

    @Override
    public boolean persist() {
        if (!isPersistent()) return false;
        FancyNpcsPlugin.get().getNpcManager().saveNpcs(true);
        return true;
    }

    @Override
    public void setPersistent(boolean persistent) {
        npc().setSaveToFile(persistent);
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return npc().getIsVisibleForPlayer().keySet().stream()
                .map(getServer()::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return getServer().getOnlinePlayers().stream()
                .filter(this::canSee)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean addViewer(Player player) {
        if (!canSee(player)) return false;
        npc().spawn(player);
        return true;
    }

    @Override
    public boolean addViewers(Collection<Player> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(Player player) {
        return npc().getIsVisibleForPlayer().containsKey(player.getUniqueId());
    }

    @Override
    public boolean canSee(Player player) {
        var plugin = FancyNpcsPlugin.get();
        var visibilityDistance = plugin.getFancyNpcConfig().getVisibilityDistance();

        if (!npc().getData().isSpawnEntity()) return false;
        if (getLocation() == null) return false;
        if (!player.getWorld().equals(getWorld())) return false;

        var distanceSquared = getLocation().distanceSquared(player.getLocation());
        if (distanceSquared > visibilityDistance * visibilityDistance) return false;

        var attribute = plugin.getAttributeManager().getAttributeByName(EntityType.PLAYER, "invisible");

        return plugin.getFancyNpcConfig().isSkipInvisibleNpcs()
               && npc().getData().getAttributes().getOrDefault(attribute, "false").equalsIgnoreCase("true")
               && !npc().getData().isGlowing()
               && npc().getData().getEquipment().isEmpty();
    }

    @Override
    public boolean isVisibleByDefault() {
        return true;
    }

    @Override
    public boolean removeViewer(Player player) {
        if (!isTrackedBy(player)) return false;
        npc().remove(player);
        return true;
    }

    @Override
    public boolean removeViewers(Collection<Player> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return FancyNpcsPlugin.get().getFancyNpcConfig().getVisibilityDistance();
    }

    @Override
    public void setDisplayRange(double range) {
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
    }
}
