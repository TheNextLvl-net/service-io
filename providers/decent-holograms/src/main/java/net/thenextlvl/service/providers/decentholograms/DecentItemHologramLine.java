package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.service.hologram.LineType;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public final class DecentItemHologramLine extends DecentHologramLine implements ItemHologramLine {
    public DecentItemHologramLine(final DecentHologram hologram, final HologramLine line) {
        super(hologram, line);
    }

    @Override
    public LineType getType() {
        return LineType.ITEM;
    }

    @Override
    public ItemStack getItemStack() {
        return line.getItem().parse(null);
    }

    @Override
    public boolean setItemStack(@Nullable final ItemStack item) {
        final var content = item != null
                ? "#ICON:" + HologramItem.fromItemStack(item).getContent()
                : "";
        if (content.equals(line.getContent())) return false;
        line.setContent(content);
        return true;
    }

    @Override
    public boolean isPlayerHead() {
        return false;
    }

    @Override
    public boolean setPlayerHead(final boolean playerHead) {
        return false;
    }

    @Override
    public ItemDisplay.ItemDisplayTransform getItemDisplayTransform() {
        return ItemDisplay.ItemDisplayTransform.FIXED;
    }

    @Override
    public boolean setItemDisplayTransform(final ItemDisplay.ItemDisplayTransform display) {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean setGlowing(final boolean glowing) {
        return false;
    }

    @Override
    public Optional<TextColor> getGlowColor() {
        return Optional.empty();
    }

    @Override
    public boolean setGlowColor(@Nullable final TextColor color) {
        return false;
    }

    @Override
    public Display.Billboard getBillboard() {
        return Display.Billboard.CENTER;
    }

    @Override
    public boolean setBillboard(final Display.Billboard billboard) {
        return false;
    }

    @Override
    public Optional<PagedHologramLine> getParentLine() {
        return Optional.ofNullable(line.getParent())
                .map(page -> new DecentPagedHologramLine(hologram, page));
    }

    @Override
    public Vector3f getOffset() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public boolean setOffset(final Vector3f offset) {
        return false;
    }
}
