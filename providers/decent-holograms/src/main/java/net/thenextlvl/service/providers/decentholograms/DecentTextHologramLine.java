package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.thenextlvl.service.api.hologram.LineType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DecentTextHologramLine extends DecentHologramLine<Component> {
    public DecentTextHologramLine(HologramLine line) {
        super(line);
    }

    @Override
    public LineType getType() {
        return LineType.TEXT;
    }

    @Override
    public Component getContent() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(line.getText());
    }

    @Override
    public void setContent(Component component) {
        line.setContent(LegacyComponentSerializer.legacyAmpersand().serialize(component));
    }
}
