package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.thenextlvl.service.api.hologram.HologramDisplay;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FancyHologramDisplay(DisplayHologramData data) implements HologramDisplay {
    @Override
    public Transformation getTransformation() {
        return new Transformation(
                new Vector3f(0), new AxisAngle4f(0, 0, 0, 0),
                data().getScale(), new AxisAngle4f(0, 0, 0, 0)
        );
    }

    @Override
    public void setTransformation(final Transformation transformation) {
        data().setScale(transformation.getScale());
    }

    @Override
    public void setTransformationMatrix(final Matrix4f transformationMatrix) {
    }

    @Override
    public int getInterpolationDuration() {
        return data().getInterpolationDuration();
    }

    @Override
    public void setInterpolationDuration(final int duration) {
        data().setInterpolationDuration(duration);
    }

    @Override
    public int getTeleportDuration() {
        return 0;
    }

    @Override
    public void setTeleportDuration(final int duration) {
    }

    @Override
    public float getViewRange() {
        return 0;
    }

    @Override
    public void setViewRange(final float range) {
    }

    @Override
    public float getShadowRadius() {
        return data().getShadowRadius();
    }

    @Override
    public void setShadowRadius(final float radius) {
        data().setShadowRadius(radius);
    }

    @Override
    public float getShadowStrength() {
        return data().getShadowStrength();
    }

    @Override
    public void setShadowStrength(final float strength) {
        data().setShadowStrength(strength);
    }

    @Override
    public float getDisplayWidth() {
        return 0;
    }

    @Override
    public void setDisplayWidth(final float width) {
    }

    @Override
    public float getDisplayHeight() {
        return 0;
    }

    @Override
    public void setDisplayHeight(final float height) {
    }

    @Override
    public int getInterpolationDelay() {
        return 0;
    }

    @Override
    public void setInterpolationDelay(final int ticks) {
    }

    @Override
    public Display.Billboard getBillboard() {
        return data().getBillboard();
    }

    @Override
    public void setBillboard(final Display.Billboard billboard) {
        data().setBillboard(billboard);
    }

    @Override
    public @Nullable Color getGlowColorOverride() {
        return null;
    }

    @Override
    public void setGlowColorOverride(@Nullable final Color color) {
    }

    @Override
    public Display.@Nullable Brightness getBrightness() {
        return data().getBrightness();
    }

    @Override
    public void setBrightness(final Display.@Nullable Brightness brightness) {
        data().setBrightness(brightness);
    }

    @Override
    public int getLineWidth() {
        return 0;
    }

    @Override
    public void setLineWidth(final int width) {
    }

    @Override
    public @Nullable Color getBackgroundColor() {
        return data() instanceof final TextHologramData text ? text.getBackground() : null;
    }

    @Override
    public void setBackgroundColor(@Nullable final Color color) {
        if (data() instanceof final TextHologramData text) text.setBackground(color);
    }

    @Override
    public byte getTextOpacity() {
        return 0;
    }

    @Override
    public void setTextOpacity(final byte opacity) {
    }

    @Override
    public boolean isShadowed() {
        return data() instanceof final TextHologramData text && text.hasTextShadow();
    }

    @Override
    public void setShadowed(final boolean shadow) {
        if (data() instanceof final TextHologramData text) text.setTextShadow(shadow);
    }

    @Override
    public boolean isSeeThrough() {
        return data() instanceof final TextHologramData text && text.isSeeThrough();
    }

    @Override
    public void setSeeThrough(final boolean seeThrough) {
        if (data() instanceof final TextHologramData text) text.setSeeThrough(seeThrough);
    }

    @Override
    public boolean isDefaultBackground() {
        return false;
    }

    @Override
    public void setDefaultBackground(final boolean defaultBackground) {
    }

    @Override
    public TextDisplay.TextAlignment getAlignment() {
        return data() instanceof final TextHologramData text ? text.getTextAlignment() : TextDisplay.TextAlignment.CENTER;
    }

    @Override
    public void setAlignment(final TextDisplay.TextAlignment alignment) {
        if (data() instanceof final TextHologramData text) text.setTextAlignment(alignment);
    }
}
