package net.thenextlvl.service.providers.decentholograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.thenextlvl.service.api.hologram.LineType;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class DecentBlockHologramLine extends DecentHologramLine<BlockData> {
    private final boolean small;

    public DecentBlockHologramLine(final HologramLine line, final boolean small) {
        super(line);
        this.small = small;
    }

    @Override
    public LineType getType() {
        return LineType.BLOCK;
    }

    @Override
    public BlockData getContent() {
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
    public void setContent(final BlockData content) {
        final var type = small ? "#SMALLHEAD:" : "#HEAD:";
        final var item = ItemStack.of(content.getMaterial());
        line.setContent(type + HologramItem.fromItemStack(item));
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
}
