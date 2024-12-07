package net.thenextlvl.service.model.hologram;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import net.thenextlvl.service.api.hologram.LineType;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DecentEntityHologramLine extends DecentHologramLine<EntityType> {
    public DecentEntityHologramLine(HologramLine line) {
        super(line);
    }

    @Override
    public LineType getType() {
        return LineType.ENTITY;
    }

    @Override
    public EntityType getContent() {
        return line.getEntity().getType();
    }

    @Override
    public void setContent(EntityType content) {
        line.setContent("#ENTITY:" + content.name());
    }
}
