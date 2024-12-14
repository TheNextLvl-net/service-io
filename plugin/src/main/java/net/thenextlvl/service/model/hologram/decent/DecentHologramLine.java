package net.thenextlvl.service.model.hologram.decent;

import net.thenextlvl.service.api.hologram.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class DecentHologramLine<T> implements HologramLine<T> {
    protected final eu.decentsoftware.holograms.api.holograms.HologramLine line;

    protected DecentHologramLine(eu.decentsoftware.holograms.api.holograms.HologramLine line) {
        this.line = line;
    }

    @Override
    public double getHeight() {
        return line.getHeight();
    }

    @Override
    public double getOffsetX() {
        return line.getOffsetX();
    }

    @Override
    public double getOffsetY() {
        return line.getOffsetY();
    }

    @Override
    public double getOffsetZ() {
        return line.getOffsetZ();
    }

    @Override
    public void setHeight(double height) {
        line.setHeight(height);
    }

    @Override
    public void setOffsetX(double offsetX) {
        line.setOffsetX(offsetX);
    }

    @Override
    public void setOffsetY(double offsetY) {
        line.setOffsetY(offsetY);
    }

    @Override
    public void setOffsetZ(double offsetZ) {
        line.setOffsetZ(offsetZ);
    }

    @Override
    public Location getLocation() {
        return line.getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    @Override
    public double getX() {
        return getLocation().getX();
    }

    @Override
    public double getY() {
        return getLocation().getY();
    }

    @Override
    public double getZ() {
        return getLocation().getZ();
    }

    @Override
    public float getPitch() {
        return getLocation().getPitch();
    }

    @Override
    public float getYaw() {
        return getLocation().getYaw();
    }
}
