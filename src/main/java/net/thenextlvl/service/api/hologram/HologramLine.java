package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Positioned;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface HologramLine<T> extends Positioned {
    LineType getType();

    /**
     * Retrieves the display associated with this hologram line.
     * This requires the capability {@link Capability#DISPLAY_BACKED}.
     *
     * @return an {@link Optional} containing the {@link HologramDisplay} if available, otherwise empty.
     */
    Optional<HologramDisplay> getDisplay();

    /**
     * Retrieves the content of the hologram line.
     *
     * @return the content of this hologram line
     */
    T getContent();

    double getHeight();

    double getOffsetX();

    double getOffsetY();

    double getOffsetZ();

    void setContent(T content);

    void setHeight(double height);

    void setOffsetX(double offsetX);

    void setOffsetY(double offsetY);

    void setOffsetZ(double offsetZ);
}
