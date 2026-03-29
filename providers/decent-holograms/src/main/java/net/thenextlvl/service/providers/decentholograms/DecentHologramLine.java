package net.thenextlvl.service.providers.decentholograms;

import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.line.StaticHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
abstract class DecentHologramLine implements StaticHologramLine {
    protected final DecentHologram hologram;
    protected final eu.decentsoftware.holograms.api.holograms.HologramLine line;

    protected DecentHologramLine(final DecentHologram hologram, final eu.decentsoftware.holograms.api.holograms.HologramLine line) {
        this.hologram = hologram;
        this.line = line;
    }

    @Override
    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        final var entityClass = getEntityType().getEntityClass();
        return entityClass != null ? entityClass : ArmorStand.class;
    }

    @Override
    public EntityType getEntityType() {
        final var entity = line.getEntity();
        return entity != null ? entity.getType() : EntityType.ARMOR_STAND;
    }

    @Override
    public World getWorld() {
        return line.getLocation().getWorld();
    }

    @Override
    public Optional<String> getViewPermission() {
        return Optional.ofNullable(line.getPermission());
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        if (Objects.equals(line.getPermission(), permission)) return false;
        line.setPermission(permission);
        return true;
    }

    @Override
    public boolean canSee(final Player player) {
        return line.canShow(player);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final DecentHologramLine that = (DecentHologramLine) o;
        return Objects.equals(line, that.line);
    }

    protected Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(line);
    }
}
