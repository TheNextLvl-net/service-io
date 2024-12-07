package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Positioned;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface HologramLine<T> extends Positioned {
    LineType getType();

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
