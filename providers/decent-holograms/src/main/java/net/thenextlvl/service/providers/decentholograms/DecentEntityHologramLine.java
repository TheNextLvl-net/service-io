package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class DecentEntityHologramLine extends DecentHologramLine implements EntityHologramLine {
    public DecentEntityHologramLine(final DecentHologram hologram, final HologramLine line) {
        super(hologram, line);
    }

    @Override
    public LineType getType() {
        return LineType.ENTITY;
    }

    @Override
    public boolean setEntityType(final EntityType entityType) {
        if (!entityType.isSpawnable()) return false;
        final var content = "#ENTITY:" + entityType.name();
        if (content.equals(line.getContent())) return false;
        line.setContent(content);
        return true;
    }

    @Override
    public double getScale() {
        return 0;
    }

    @Override
    public boolean setScale(final double scale) {
        return false;
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
        return Display.Billboard.CENTER;
    }

    @Override
    public boolean setBillboard(final Display.Billboard billboard) {
        return false;
    }

    @Override
    public Optional<PagedHologramLine> getParentLine() {
        return Optional.ofNullable(line.getParent())
                .map(page -> new DecentPagedHologramLine(hologram, page));
    }

    @Override
    public Vector3f getOffset() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public boolean setOffset(final Vector3f offset) {
        return false;
    }
}
