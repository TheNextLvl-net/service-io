package net.thenextlvl.service.model.character.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.thenextlvl.service.api.npc.Character;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

@NullMarked
public record CitizensCharacter(NPC npc) implements Character {
    @Override
    public String getName() {
        return getEntity().getName();
    }

    @Override
    public boolean isPersistent() {
        return getEntity().isPersistent();
    }

    @Override
    public boolean persist() {
        CitizensAPI.getNPCRegistry().saveToStore();
        return true;
    }

    @Override
    public void setPersistent(boolean persistent) {
        getEntity().setPersistent(persistent);
    }

    @Override
    public @Unmodifiable Set<Player> getTrackedBy() {
        return getEntity().getTrackedBy();
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return Set.of();
    }

    @Override
    public boolean addViewer(Player player) {
        return npc().getTraitOptional(PlayerFilter.class).toJavaUtil()
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
        return getEntity().getTrackedBy().contains(player);
    }

    @Override
    public boolean canSee(Player player) {
        return !npc().isHiddenFrom(player);
    }

    @Override
    public boolean isVisibleByDefault() {
        return getEntity().isVisibleByDefault();
    }

    @Override
    public boolean removeViewer(Player player) {
        return npc().getTraitOptional(PlayerFilter.class).toJavaUtil()
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
        return npc().getTraitOptional(PlayerFilter.class).toJavaUtil()
                .map(PlayerFilter::getApplyRange)
                .orElse(-1d);
    }

    @Override
    public void setDisplayRange(double range) {
        npc().getTraitOptional(PlayerFilter.class).toJavaUtil()
                .ifPresent(filter -> filter.setApplyRange(range));
    }

    @Override
    public void setVisibleByDefault(boolean visible) {
        getEntity().setVisibleByDefault(visible);
    }

    @Override
    public Location getLocation() {
        return getEntity().getLocation();
    }

    @Override
    public Server getServer() {
        return getEntity().getServer();
    }

    @Override
    public World getWorld() {
        return getEntity().getWorld();
    }

    @Override
    public double getX() {
        return getEntity().getX();
    }

    @Override
    public double getY() {
        return getEntity().getY();
    }

    @Override
    public double getZ() {
        return getEntity().getZ();
    }

    @Override
    public float getPitch() {
        return getEntity().getPitch();
    }

    @Override
    public float getYaw() {
        return getEntity().getYaw();
    }

    @Override
    public Entity getEntity() {
        return npc().getEntity();
    }
}
