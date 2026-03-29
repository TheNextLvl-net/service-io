package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.format.TextColor;
import net.thenextlvl.service.api.hologram.Hologram;
import net.thenextlvl.service.api.hologram.LineType;
import net.thenextlvl.service.api.hologram.line.BlockHologramLine;
import net.thenextlvl.service.api.hologram.line.PagedHologramLine;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@NullMarked
public final class DecentBlockHologramLine extends DecentHologramLine implements BlockHologramLine {
    private final boolean small;

    public DecentBlockHologramLine(final Hologram hologram, final HologramLine line, final boolean small) {
        super(hologram, line);
        this.small = small;
    }

    @Override
    public LineType getType() {
        return LineType.BLOCK;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final DecentBlockHologramLine that = (DecentBlockHologramLine) o;
        return small == that.small;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), small);
    }

    @Override
    public BlockData getBlock() {
        final var item = line.getItem().parse(null);
        return item.getType().createBlockData(blockData -> {
            if (!(blockData instanceof final Skull skull)) return;
            final var data = item.getData(DataComponentTypes.PROFILE);
            if (data == null) return;
            final var profile = getServer().createProfile(data.uuid(), data.name());
            skull.setPlayerProfile(profile);
        });
    }

    @Override
    public boolean setBlock(final BlockData block) {
        final var type = small ? "#SMALLHEAD:" : "#HEAD:";
        final var item = ItemStack.of(block.getMaterial());
        final var content = type + HologramItem.fromItemStack(item).getContent();
        if (content.equals(line.getContent())) return false;
        line.setContent(content);
        return true;
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
                .map(hologramPage -> new DecentPagedHologramLine(hologramPage));
    }

    @Override
    public Vector3f getOffset() {
        return null;
    }

    @Override
    public boolean setOffset(final Vector3f offset) {
        return false;
    }
}
