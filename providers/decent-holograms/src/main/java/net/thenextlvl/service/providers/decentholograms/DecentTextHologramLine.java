package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.thenextlvl.service.api.hologram.LineType;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import net.thenextlvl.service.api.hologram.line.TextHologramLine;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class DecentTextHologramLine extends DecentHologramLine implements TextHologramLine {
    public DecentTextHologramLine(final DecentHologram hologram, final HologramLine line) {
        super(hologram, line);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Optional<Component> getText(final Player player) {
        final var text = line.getText();
        if (text == null || text.isEmpty()) return Optional.empty();
        return Optional.of(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
    }

    @Override
    public Optional<String> getUnparsedText() {
        final var text = line.getText();
        if (text == null || text.isEmpty()) return Optional.empty();
        return Optional.of(text);
    }

    @Override
    public boolean setText(@Nullable final Component text) {
        final var content = text != null
                ? LegacyComponentSerializer.legacyAmpersand().serialize(text)
                : "";
        if (content.equals(line.getContent())) return false;
        line.setContent(content);
        return true;
    }

    @Override
    public boolean setUnparsedText(@Nullable final String text) {
        final var content = text != null ? text : "";
        if (content.equals(line.getContent())) return false;
        line.setContent(content);
        return true;
    }

    @Override
    public int getLineWidth() {
        return 200;
    }

    @Override
    public boolean setLineWidth(final int width) {
        return false;
    }

    @Override
    public Optional<Color> getBackgroundColor() {
        return Optional.empty();
    }

    @Override
    public boolean setBackgroundColor(@Nullable final Color color) {
        return false;
    }

    @Override
    public int getTextOpacity() {
        return 100;
    }

    @Override
    public boolean setTextOpacity(final int opacity) {
        return false;
    }

    @Override
    public boolean isShadowed() {
        return false;
    }

    @Override
    public boolean setShadowed(final boolean shadow) {
        return false;
    }

    @Override
    public boolean isSeeThrough() {
        return false;
    }

    @Override
    public boolean setSeeThrough(final boolean seeThrough) {
        return false;
    }

    @Override
    public boolean isDefaultBackground() {
        return true;
    }

    @Override
    public boolean setDefaultBackground(final boolean defaultBackground) {
        return false;
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return TextDisplay.TextAlignment.CENTER;
    }

    @Override
    public boolean setAlignment(final TextDisplay.TextAlignment alignment) {
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
