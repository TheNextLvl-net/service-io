package net.thenextlvl.service.providers.fancyholograms.v3;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.LineType;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import net.thenextlvl.service.api.hologram.line.StaticHologramLine;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
abstract class FancyHologramLine<D extends DisplayHologramData> implements StaticHologramLine {
    protected final D data;
    protected final FancyHologram hologram;

    protected FancyHologramLine(final FancyHologram hologram, final D data) {
        this.hologram = hologram;
        this.data = data;
    }

    @Override
    public Hologram getHologram() {
        return hologram;
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
    public World getWorld() {
        return data.getLocation().getWorld();
    }

    @Override
    public Optional<String> getViewPermission() {
        return Optional.empty();
    }

    @Override
    public boolean setViewPermission(@Nullable final String permission) {
        return false;
    }

    @Override
    public boolean canSee(final Player player) {
        return true;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean setGlowing(final boolean glowing) {
        return false;
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.empty();
    }

    @Override
    public boolean setGlowColor(@Nullable final TextColor color) {
        return false;
    }

    @Override
    public Display.Billboard getBillboard() {
        return data.getBillboard();
    }

    @Override
    public boolean setBillboard(final Display.Billboard billboard) {
        if (data.getBillboard().equals(billboard)) return false;
        data.setBillboard(billboard);
        return true;
    }

    @Override
    public Optional<PagedHologramLine> getParentLine() {
        return Optional.empty();
    }

    @Override
    public Vector3f getOffset() {
        return data.getTranslation();
    }

    @Override
    public boolean setOffset(final Vector3f offset) {
        if (data.getTranslation().equals(offset)) return false;
        data.setTranslation(offset);
        return true;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final FancyHologramLine<?> that = (FancyHologramLine<?>) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
