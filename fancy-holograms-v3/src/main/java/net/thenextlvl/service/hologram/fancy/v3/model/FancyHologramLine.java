package net.thenextlvl.service.hologram.fancy.v3.model;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import net.thenextlvl.service.api.hologram.HologramDisplay;
import net.thenextlvl.service.api.hologram.HologramLine;
import net.thenextlvl.service.api.hologram.LineType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.Optional;

@NullMarked
abstract class FancyHologramLine<D extends DisplayHologramData, T> implements HologramLine<T> {
    public final D data;

    protected FancyHologramLine(D data) {
        this.data = data;
    }

    @Override
    public LineType getType() {
        return switch (data.getType()) {
            case TEXT -> LineType.TEXT;
            case ITEM -> LineType.ITEM;
            case BLOCK -> LineType.BLOCK;
        };
    }

    @Override
    public Optional<HologramDisplay> getDisplay() {
        return Optional.of(new FancyHologramDisplay(data));
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public double getOffsetX() {
        return data.getTranslation().x();
    }

    @Override
    public double getOffsetY() {
        return data.getTranslation().y();
    }

    @Override
    public double getOffsetZ() {
        return data.getTranslation().z();
    }

    @Override
    public void setHeight(double height) {
    }

    @Override
    public void setOffsetX(double offsetX) {
        data.setTranslation(new Vector3f(
                (float) offsetX,
                (float) getOffsetY(),
                (float) getOffsetZ())
        );
    }

    @Override
    public void setOffsetY(double offsetY) {
        data.setTranslation(new Vector3f(
                (float) getOffsetX(),
                (float) offsetY,
                (float) getOffsetZ())
        );
    }

    @Override
    public void setOffsetZ(double offsetZ) {
        data.setTranslation(new Vector3f(
                (float) getOffsetX(),
                (float) getOffsetY(),
                (float) offsetZ)
        );
    }

    @Override
    public Location getLocation() {
        return data.getLocation();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public World getWorld() {
        return data.getLocation().getWorld();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FancyHologramLine<?, ?> that = (FancyHologramLine<?, ?>) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
