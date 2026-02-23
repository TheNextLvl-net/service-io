package net.thenextlvl.service.providers.fancyholograms.v2;

import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public final class FancyTextHologramLine extends FancyHologramLine<TextHologramData, Component> {
    public FancyTextHologramLine(final TextHologramData data) {
        super(data);
    }

    @Override
    public Component getContent() {
        return Component.join(JoinConfiguration.newlines(), data.getText().stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .toList());
    }

    @Override
    public void setContent(final Component content) {
        final var lines = MiniMessage.miniMessage().serialize(content).split("\\\\n|<newline>|<br>");
        data.setText(Arrays.asList(lines));
    }
}
