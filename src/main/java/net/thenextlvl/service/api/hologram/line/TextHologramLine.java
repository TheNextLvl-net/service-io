package net.thenextlvl.service.api.hologram.line;

import net.kyori.adventure.text.Component;
import net.thenextlvl.service.api.hologram.HologramCapability;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a line type within a hologram that displays text content.
 *
 * @see HologramCapability#TEXT_LINES
 * @since 3.0.0
 */
public interface TextHologramLine extends StaticHologramLine {
    /**
     * Gets the displayed text.
     *
     * @param player the player for which to get the text
     * @return the displayed text, or empty if not set
     * @since 3.0.0
     */
    Optional<Component> getText(Player player);

    /**
     * Gets the displayed text in {@link net.kyori.adventure.text.minimessage.MiniMessage} format.
     *
     * @return the displayed text, or empty if not set
     * @since 3.0.0
     */
    Optional<String> getUnparsedText();

    /**
     * Sets the displayed text.
     *
     * @param text the new text, or null to unset
     * @return {@code true} if the text was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setText(@Nullable Component text);

    /**
     * Sets the displayed text in {@link net.kyori.adventure.text.minimessage.MiniMessage} format.
     *
     * @param text the new text, or null to unset
     * @return {@code true} if the text was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setUnparsedText(@Nullable String text);

    /**
     * Gets the maximum line width before wrapping.
     *
     * @return the line width
     * @since 3.0.0
     */
    int getLineWidth();

    /**
     * Sets the maximum line width before wrapping.
     *
     * @param width the new line width
     * @return {@code true} if the line width was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setLineWidth(int width);

    /**
     * Gets the text background color.
     *
     * @return the background color, or empty if not set
     * @since 3.0.0
     */
    Optional<Color> getBackgroundColor();

    /**
     * Sets the text background color.
     *
     * @param color the new background color, or null to unset
     * @return {@code true} if the background color was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setBackgroundColor(@Nullable Color color);

    /**
     * Gets the text opacity in percent.
     *
     * @return the opacity
     * @since 3.0.0
     */
    int getTextOpacity();

    /**
     * Sets the text opacity in percent.
     *
     * @param opacity the new opacity
     * @return {@code true} if the opacity was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setTextOpacity(int opacity);

    /**
     * Gets if the text is shadowed.
     *
     * @return the shadow status
     * @since 3.0.0
     */
    boolean isShadowed();

    /**
     * Sets if the text is shadowed.
     *
     * @param shadow if shadowed
     * @return {@code true} if the shadow status was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setShadowed(boolean shadow);

    /**
     * Gets if the text is see through.
     *
     * @return the see through status
     * @since 3.0.0
     */
    boolean isSeeThrough();

    /**
     * Sets if the text is see through.
     *
     * @param seeThrough if see through
     * @return {@code true} if the see through status was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setSeeThrough(boolean seeThrough);

    /**
     * Gets if the text has its default background.
     *
     * @return the default background status
     * @since 3.0.0
     */
    boolean isDefaultBackground();

    /**
     * Sets if the text has its default background.
     *
     * @param defaultBackground if default
     * @return {@code true} if the default background status was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setDefaultBackground(boolean defaultBackground);

    /**
     * Gets the text alignment for this display.
     *
     * @return the text alignment
     * @since 3.0.0
     */
    TextAlignment getAlignment();

    /**
     * Sets the text alignment for this display.
     *
     * @param alignment the new alignment
     * @return {@code true} if the alignment was changed, {@code false} otherwise
     * @since 3.0.0
     */
    boolean setAlignment(TextAlignment alignment);
}
