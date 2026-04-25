package net.thenextlvl.service.providers.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.service.character.Character;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public record FancyCharacter(Npc npc) implements Character {
    @Override
    public CompletableFuture<Boolean> teleportAsync(final Location location) {
        npc().getData().setLocation(location);
        npc().moveForAll(false);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Component getDisplayName() {
        return MiniMessage.miniMessage().deserialize(npc.getData().getDisplayName());
    }

    @Override
    public Optional<Entity> getEntity() {
        return Optional.empty();
    }

    @Override
    public EntityType getType() {
        return npc().getData().getType();
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.ofNullable(npc().getData().getLocation());
    }

    @Override
    public Optional<World> getWorld() {
        return getLocation().map(Location::getWorld);
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
    public void setCollidable(final boolean collidable) {
        npc().getData().setCollidable(collidable);
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
    public boolean spawn(final Location location) {
        npc().getData().setLocation(location);
        npc().getData().setSpawnEntity(true);
        npc().spawnForAll();
        return true;
    }

    @Override
    public void lookAt(final Entity entity) {
        lookAt(entity.getLocation());
    }

    @Override
    public void lookAt(final Location target) {
        final var location = npc().getData().getLocation().clone();
        final var direction = location.setDirection(target.clone().subtract(location).toVector());
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
    public void setDisplayName(final Component displayName) {
        npc().getData().setDisplayName(MiniMessage.miniMessage().serialize(displayName));
        npc().updateForAll();
    }

    @Override
    public void setInvulnerable(final boolean invulnerable) {
    }

    @Override
    public void setTablistEntryHidden(final boolean hidden) {
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
    public boolean setPersistent(final boolean persistent) {
        if (isPersistent() == persistent) return false;
        npc().setSaveToFile(persistent);
        return true;
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return npc().getIsVisibleForPlayer().keySet().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<UUID> getViewers() {
        return Set.copyOf(npc().getIsVisibleForPlayer().keySet());
    }

    @Override
    public boolean addViewer(final UUID player) {
        final var viewer = Bukkit.getPlayer(player);
        if (viewer == null || !canSee(viewer)) return false;
        npc().spawn(viewer);
        return true;
    }

    @Override
    public boolean addViewers(final Collection<UUID> players) {
        return players.stream().map(this::addViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public boolean isTrackedBy(final Player player) {
        return npc().getIsVisibleForPlayer().containsKey(player.getUniqueId());
    }

    @Override
    public boolean isViewer(final UUID player) {
        return getViewers().contains(player);
    }

    @Override
    public boolean canSee(final Player player) {
        final var plugin = FancyNpcsPlugin.get();
        final var visibilityDistance = plugin.getFancyNpcConfig().getVisibilityDistance();

        if (!npc().getData().isSpawnEntity()) return false;
        final var location = getLocation().orElse(null);
        if (location == null) return false;
        if (!player.getWorld().equals(location.getWorld())) return false;

        final var distanceSquared = location.distanceSquared(player.getLocation());
        if (distanceSquared > visibilityDistance * visibilityDistance) return false;

        final var attribute = plugin.getAttributeManager().getAttributeByName(EntityType.PLAYER, "invisible");

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
    public boolean removeViewer(final UUID player) {
        final var viewer = Bukkit.getPlayer(player);
        if (viewer == null || !isTrackedBy(viewer)) return false;
        npc().remove(viewer);
        return true;
    }

    @Override
    public boolean removeViewers(final Collection<UUID> players) {
        return players.stream().map(this::removeViewer).reduce(false, Boolean::logicalOr);
    }

    @Override
    public double getDisplayRange() {
        return FancyNpcsPlugin.get().getFancyNpcConfig().getVisibilityDistance();
    }

    @Override
    public void setDisplayRange(final double range) {
    }

    @Override
    public boolean setVisibleByDefault(final boolean visible) {
        return false;
    }
}
