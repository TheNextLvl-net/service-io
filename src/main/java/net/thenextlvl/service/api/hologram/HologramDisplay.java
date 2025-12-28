package net.thenextlvl.service.api.hologram;

import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

/**
 * Represents a display capable of rendering holograms with various configuration
 * options such as transformations, rendering details, and visual appearance.
 * <p>
 * This interface defines methods for manipulating the behavior and appearance of
 * hologram displays, including settings for transformations, interpolation, viewing
 * range, shadow properties, dimensions, text rendering settings, and more.
 *
 * @see Display
 * @see TextDisplay
 * @since 2.2.0
 */
public interface HologramDisplay {
    /**
     * Gets the transformation applied to this display.
     *
     * @return the transformation
     */
    Transformation getTransformation();

    /**
     * Sets the transformation applied to this display
     *
     * @param transformation the new transformation
     */
    void setTransformation(Transformation transformation);

    /**
     * Sets the raw transformation matrix applied to this display
     *
     * @param transformationMatrix the transformation matrix
     */
    void setTransformationMatrix(Matrix4f transformationMatrix);

    /**
     * Gets the interpolation duration of this display.
     *
     * @return interpolation duration
     */
    int getInterpolationDuration();

    /**
     * Sets the interpolation duration of this display.
     *
     * @param duration new duration
     */
    void setInterpolationDuration(int duration);

    /**
     * Gets the teleport duration of this display.
     * <ul>
     *     <li>0 means that updates are applied immediately.</li>
     *     <li>1 means that the display entity will move from current position to the updated one over one tick.</li>
     *     <li>Higher values spread the movement over multiple ticks.</li>
     * </ul>
     *
     * @return teleport duration
     */
    int getTeleportDuration();

    /**
     * Sets the teleport duration of this display.
     *
     * @param duration new duration
     * @throws IllegalArgumentException if duration is not between 0 and 59
     * @see #getTeleportDuration()
     */
    void setTeleportDuration(int duration);

    /**
     * Gets the view distance/range of this display.
     *
     * @return view range
     */
    float getViewRange();

    /**
     * Sets the view distance/range of this display.
     *
     * @param range new range
     */
    void setViewRange(float range);

    /**
     * Gets the shadow radius of this display.
     *
     * @return radius
     */
    float getShadowRadius();

    /**
     * Sets the shadow radius of this display.
     *
     * @param radius new radius
     */
    void setShadowRadius(float radius);

    /**
     * Gets the shadow strength of this display.
     *
     * @return shadow strength
     */
    float getShadowStrength();

    /**
     * Sets the shadow strength of this display.
     *
     * @param strength new strength
     */
    void setShadowStrength(float strength);

    /**
     * Gets the width of this display.
     *
     * @return width
     */
    float getDisplayWidth();

    /**
     * Sets the width of this display.
     *
     * @param width new width
     */
    void setDisplayWidth(float width);

    /**
     * Gets the height of this display.
     *
     * @return height
     */
    float getDisplayHeight();

    /**
     * Sets the height if this display.
     *
     * @param height new height
     */
    void setDisplayHeight(float height);

    /**
     * Gets the amount of ticks before client-side interpolation will commence.
     *
     * @return interpolation delay ticks
     */
    int getInterpolationDelay();

    /**
     * Sets the amount of ticks before client-side interpolation will commence.
     *
     * @param ticks interpolation delay ticks
     */
    void setInterpolationDelay(int ticks);

    /**
     * Gets the billboard setting of this entity.
     * <p>
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @return billboard setting
     */
    Display.Billboard getBillboard();

    /**
     * Sets the billboard setting of this entity.
     * <p>
     * The billboard setting controls the automatic rotation of the entity to
     * face the player.
     *
     * @param billboard new setting
     */
    void setBillboard(Display.Billboard billboard);

    /**
     * Gets the scoreboard team overridden glow color of this display.
     *
     * @return glow color
     */
    @Nullable
    Color getGlowColorOverride();

    /**
     * Sets the scoreboard team overridden glow color of this display.
     *
     * @param color new color
     */
    void setGlowColorOverride(@Nullable Color color);

    /**
     * Gets the brightness override of the entity.
     *
     * @return brightness override, if set
     */
    Display.@Nullable Brightness getBrightness();

    /**
     * Sets the brightness override of the entity.
     *
     * @param brightness new brightness override
     */
    void setBrightness(Display.@Nullable Brightness brightness);

    /**
     * Gets the maximum line width before wrapping.
     *
     * @return the line width
     */
    int getLineWidth();

    /**
     * Sets the maximum line width before wrapping.
     *
     * @param width new line width
     */
    void setLineWidth(int width);

    /**
     * Gets the text background color.
     *
     * @return the background color
     */
    @Nullable
    Color getBackgroundColor();

    /**
     * Sets the text background color.
     *
     * @param color new background color
     */
    void setBackgroundColor(@Nullable Color color);

    /**
     * Gets the text opacity.
     *
     * @return opacity or -1 if not set
     */
    byte getTextOpacity();

    /**
     * Sets the text opacity.
     *
     * @param opacity new opacity or -1 if default
     */
    void setTextOpacity(byte opacity);

    /**
     * Gets if the text is shadowed.
     *
     * @return shadow status
     */
    boolean isShadowed();

    /**
     * Sets if the text is shadowed.
     *
     * @param shadow if shadowed
     */
    void setShadowed(boolean shadow);

    /**
     * Gets if the text is see through.
     *
     * @return see through status
     */
    boolean isSeeThrough();

    /**
     * Sets if the text is see through.
     *
     * @param seeThrough if see through
     */
    void setSeeThrough(boolean seeThrough);

    /**
     * Gets if the text has its default background.
     *
     * @return default background
     */
    boolean isDefaultBackground();

    /**
     * Sets if the text has its default background.
     *
     * @param defaultBackground if default
     */
    void setDefaultBackground(boolean defaultBackground);

    /**
     * Gets the text alignment for this display.
     *
     * @return text alignment
     */
    TextDisplay.TextAlignment getAlignment();

    /**
     * Sets the text alignment for this display.
     *
     * @param alignment new alignment
     */
    void setAlignment(TextDisplay.TextAlignment alignment);
}
