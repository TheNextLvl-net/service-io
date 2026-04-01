package net.thenextlvl.service.hologram.line;

import net.thenextlvl.service.hologram.HologramCapability;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a hologram line that is backed by a display entity.
 * <p>
 * This interface is not part of the main line type hierarchy.
 * Instead, it acts as a mixin that implementations may additionally implement
 * when the hologram provider uses display entities (e.g., {@code TextDisplay},
 * {@code ItemDisplay}, {@code BlockDisplay}).
 * <p>
 * Whether a provider is display-backed can be checked using the
 * {@link HologramCapability#DISPLAY_BACKED} capability or by testing
 * {@code line instanceof DisplayHologramLine}.
 *
 * @see HologramCapability#DISPLAY_BACKED
 * @since 3.0.0
 */
public interface DisplayHologramLine extends StaticHologramLine {
    /**
     * Gets the transformation applied to this display.
     *
     * @return the transformation
     * @since 3.0.0
     */
    Transformation getTransformation();

    /**
     * Sets the transformation applied to this display.
     *
     * @param transformation the new transformation
     * @return {@code true} if the transformation was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setTransformation(Transformation transformation);

    /**
     * Sets the raw transformation matrix applied to this display.
     *
     * @param transformationMatrix the transformation matrix
     * @return {@code true} if the transformation matrix was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setTransformationMatrix(Matrix4f transformationMatrix);

    /**
     * Gets the interpolation duration of this display.
     *
     * @return the interpolation duration
     * @since 3.0.0
     */
    int getInterpolationDuration();

    /**
     * Sets the interpolation duration of this display.
     *
     * @param duration the new duration
     * @return {@code true} if the interpolation duration was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setInterpolationDuration(int duration);

    /**
     * Gets the teleport duration of this display.
     * <ul>
     *     <li>0 means that updates are applied immediately.</li>
     *     <li>1 means that the display entity will move from current position to the updated one over one tick.</li>
     *     <li>Higher values spread the movement over multiple ticks.</li>
     * </ul>
     *
     * @return the teleport duration
     * @since 3.0.0
     */
    int getTeleportDuration();

    /**
     * Sets the teleport duration of this display.
     *
     * @param duration the new duration
     * @return {@code true} if the teleport duration was changed, {@code false} otherwise
     * @throws IllegalArgumentException if duration is not between 0 and 59
     * @see #getTeleportDuration()
     * @since 3.0.0
     */
    boolean setTeleportDuration(int duration);

    /**
     * Gets the view distance/range of this display.
     *
     * @return the view range
     * @since 3.0.0
     */
    float getViewRange();

    /**
     * Sets the view distance/range of this display.
     *
     * @param range the new range
     * @return {@code true} if the view range was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setViewRange(float range);

    /**
     * Gets the shadow radius of this display.
     *
     * @return the shadow radius
     * @since 3.0.0
     */
    float getShadowRadius();

    /**
     * Sets the shadow radius of this display.
     *
     * @param radius the new radius
     * @return {@code true} if the shadow radius was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setShadowRadius(float radius);

    /**
     * Gets the shadow strength of this display.
     *
     * @return the shadow strength
     * @since 3.0.0
     */
    float getShadowStrength();

    /**
     * Sets the shadow strength of this display.
     *
     * @param strength the new strength
     * @return {@code true} if the shadow strength was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setShadowStrength(float strength);

    /**
     * Gets the width of this display.
     *
     * @return the display width
     * @since 3.0.0
     */
    float getDisplayWidth();

    /**
     * Sets the width of this display.
     *
     * @param width the new width
     * @return {@code true} if the display width was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setDisplayWidth(float width);

    /**
     * Gets the height of this display.
     *
     * @return the display height
     * @since 3.0.0
     */
    float getDisplayHeight();

    /**
     * Sets the height of this display.
     *
     * @param height the new height
     * @return {@code true} if the display height was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setDisplayHeight(float height);

    /**
     * Gets the amount of ticks before client-side interpolation will commence.
     *
     * @return the interpolation delay in ticks
     * @since 3.0.0
     */
    int getInterpolationDelay();

    /**
     * Sets the amount of ticks before client-side interpolation will commence.
     *
     * @param ticks the interpolation delay in ticks
     * @return {@code true} if the interpolation delay was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setInterpolationDelay(int ticks);

    /**
     * Gets the brightness override of the entity.
     *
     * @return the brightness override, or empty if not set
     * @since 3.0.0
     */
    Optional<Display.Brightness> getBrightness();

    /**
     * Sets the brightness override of the entity.
     *
     * @param brightness the new brightness override, or null to unset
     * @return {@code true} if the brightness was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setBrightness(Display.@Nullable Brightness brightness);
}
