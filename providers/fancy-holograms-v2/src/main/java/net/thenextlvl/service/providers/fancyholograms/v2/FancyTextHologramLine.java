package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@NullMarked
public final class FancyTextHologramLine extends FancyHologramLine<TextHologramData> implements TextHologramLine {
    public FancyTextHologramLine(final FancyHologram hologram, final TextHologramData data) {
        super(hologram, data);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Optional<Component> getText(final Player player) {
        final var text = data.getText();
        if (text == null || text.isEmpty()) return Optional.empty();
        return Optional.of(Component.join(JoinConfiguration.newlines(), text.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .toList()));
    }

    @Override
    public Optional<String> getUnparsedText() {
        final var text = data.getText();
        if (text == null || text.isEmpty()) return Optional.empty();
        return Optional.of(String.join("\n", text));
    }

    @Override
    public boolean setText(@Nullable final Component text) {
        if (text == null) {
            if (data.getText().isEmpty()) return false;
            data.setText(java.util.List.of());
            return true;
        }
        final var lines = Arrays.asList(MiniMessage.miniMessage().serialize(text).split("\\\\n|<newline>|<br>"));
        if (lines.equals(data.getText())) return false;
        data.setText(lines);
        return true;
    }

    @Override
    public boolean setUnparsedText(@Nullable final String text) {
        if (text == null) {
            if (data.getText().isEmpty()) return false;
            data.setText(java.util.List.of());
            return true;
        }
        final var lines = Arrays.asList(text.split("\n"));
        if (lines.equals(data.getText())) return false;
        data.setText(lines);
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
        return Optional.ofNullable(data.getBackground());
    }

    @Override
    public boolean setBackgroundColor(@Nullable final Color color) {
        if (Objects.equals(data.getBackground(), color)) return false;
        data.setBackground(color);
        return true;
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
        return data.hasTextShadow();
    }

    @Override
    public boolean setShadowed(final boolean shadow) {
        if (data.hasTextShadow() == shadow) return false;
        data.setTextShadow(shadow);
        return true;
    }

    @Override
    public boolean isSeeThrough() {
        return data.isSeeThrough();
    }

    @Override
    public boolean setSeeThrough(final boolean seeThrough) {
        if (data.isSeeThrough() == seeThrough) return false;
        data.setSeeThrough(seeThrough);
        return true;
    }

    @Override
    public boolean isDefaultBackground() {
        return false;
    }

    @Override
    public boolean setDefaultBackground(final boolean defaultBackground) {
        return false;
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return data.getTextAlignment();
    }

    @Override
    public boolean setAlignment(final TextDisplay.TextAlignment alignment) {
        if (data.getTextAlignment() == alignment) return false;
        data.setTextAlignment(alignment);
        return true;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return TextDisplay.class;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TEXT_DISPLAY;
    }
}
