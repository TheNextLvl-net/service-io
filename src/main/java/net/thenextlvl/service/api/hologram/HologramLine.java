package net.thenextlvl.service.api.hologram;

import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public interface HologramLine<T> extends Viewable {
    EntityType getEntityType();

    LineType getType();

    T getContent();

    UUID getUniqueId();

    boolean isGlowing();

    double getHeight();

    double getOffset();

    int getEntityId();

    void remove();

    void setContent(T content);

    void setGlowing(boolean glowing);

    void setHeight(double height);

    void setOffset(double offset);
}
