package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Positioned;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Represents a line within a hologram which can have varying content types and positional attributes.
 *
 * @param <T> the type of content associated with the hologram line
 */
@NullMarked
public interface HologramLine<T> extends Positioned {
    /**
     * Retrieves the type of the hologram line.
     *
     * @return the type of the line, which is one of the defined constants in the {@link LineType} enum.
     */
    LineType getType();

    @Override
    Location getLocation();

    /**
     * Retrieves the display associated with this hologram line.
     * This requires the capability {@link HologramCapability#DISPLAY_BACKED}.
     *
     * @return an {@link Optional} containing the {@link HologramDisplay} if available, otherwise empty.
     */
    Optional<HologramDisplay> getDisplay();

    @Override
    World getWorld();

    /**
     * Retrieves the content of the hologram line.
     *
     * @return the content of this hologram line
     */
    T getContent();

    /**
     * Retrieves the height of the hologram line.
     *
     * @return the height of the hologram line as a double value
     */
    double getHeight();

    /**
     * Retrieves the horizontal offset (X-axis) of this hologram line.
     *
     * @return the X-axis offset as a double value
     */
    double getOffsetX();

    /**
     * Retrieves the vertical offset (Y-axis) of this hologram line.
     *
     * @return the Y-axis offset as a double value
     */
    double getOffsetY();

    /**
     * Retrieves the depth offset (Z-axis) of this hologram line.
     *
     * @return the Z-axis offset as a double value
     */
    double getOffsetZ();

    /**
     * Sets the content of this hologram line.
     *
     * @param content the content to be set for this hologram line
     */
    void setContent(T content);

    /**
     * Sets the height of the hologram line.
     *
     * @param height the new height to set for this hologram line
     */
    void setHeight(double height);

    /**
     * Sets the horizontal offset (X-axis) for the hologram line.
     *
     * @param offsetX the new horizontal offset to set, represented as a double value
     */
    void setOffsetX(double offsetX);

    /**
     * Sets the vertical offset (Y-axis) for the hologram line.
     *
     * @param offsetY the new vertical offset to set, represented as a double value
     */
    void setOffsetY(double offsetY);

    /**
     * Sets the depth offset (Z-axis) for the hologram line.
     *
     * @param offsetZ the new depth offset to set, represented as a double value
     */
    void setOffsetZ(double offsetZ);
}
