package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.hologram.line.BlockHologramLine;
import net.thenextlvl.service.hologram.line.DisplayHologramLine;
import net.thenextlvl.service.hologram.line.EntityHologramLine;
import net.thenextlvl.service.hologram.line.HologramLine;
import net.thenextlvl.service.hologram.line.ItemHologramLine;
import net.thenextlvl.service.hologram.line.PagedHologramLine;
import net.thenextlvl.service.hologram.line.StaticHologramLine;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

final class HologramConverter extends Converter<HologramController> {
    public HologramConverter(final Plugin plugin, final HologramController source, final HologramController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        for (final Hologram hologram : source.getHolograms()) {
            final var created = target.createHologram(hologram.getName(), hologram.getLocation());
            hologram.forEach(hologramLine -> {
                try {
                    switch (hologramLine) {
                        case final TextHologramLine text -> copyFromText(text, created.addTextLine());
                        case final ItemHologramLine item -> copyFromItem(item, created.addItemLine());
                        case final BlockHologramLine block -> copyFromBlock(block, created.addBlockLine());
                        case final EntityHologramLine entity -> {
                            final var newLine = created.addEntityLine(entity.getEntityType());
                            copyFromEntity(entity, newLine);
                        }
                        case final PagedHologramLine paged -> copyFromPaged(paged, created.addPagedLine());
                        default -> throw new UnsupportedOperationException(
                                "Unknown hologram line type: " + hologramLine.getClass()
                        );
                    }
                } catch (final CapabilityException e) {
                    plugin.getComponentLogger().warn(
                            "Failed to convert hologram line from {} to {}",
                            source.getName(), target.getName(), e
                    );
                }
            });
            created.addViewers(hologram.getViewers());
            created.setPersistent(hologram.isPersistent());
            created.setVisibleByDefault(hologram.isVisibleByDefault());
            created.setViewPermission(hologram.getViewPermission().orElse(null));
            created.setPersistent(hologram.isPersistent());
            created.persist();
        }
        return CompletableFuture.completedFuture(null);
    }

    private void copyFromPaged(final PagedHologramLine paged, final PagedHologramLine newLine) {
        newLine.setInterval(paged.getInterval());
        newLine.setPaused(paged.isPaused());
        newLine.setRandomOrder(paged.isRandomOrder());

        copyFrom(paged, newLine);

        paged.forEachPage(hologramLine -> {
            try {
                switch (hologramLine) {
                    case final TextHologramLine text -> copyFromText(text, newLine.addTextPage());
                    case final ItemHologramLine item -> copyFromItem(item, newLine.addItemPage());
                    case final BlockHologramLine block -> copyFromBlock(block, newLine.addBlockPage());
                    case final EntityHologramLine entity -> {
                        final var newEntity = newLine.addEntityPage(entity.getEntityType());
                        copyFromEntity(entity, newEntity);
                    }
                    default -> throw new UnsupportedOperationException(
                            "Unknown hologram line type: " + hologramLine.getClass()
                    );
                }
            } catch (final CapabilityException e) {
                plugin.getComponentLogger().warn(
                        "Failed to convert hologram line from {} to {}",
                        source.getName(), target.getName(), e
                );
            }
        });
    }

    private void copyFrom(final HologramLine paged, final HologramLine newLine) {
        newLine.setViewPermission(paged.getViewPermission().orElse(null));
    }

    private void copyFromText(final TextHologramLine text, final TextHologramLine newLine) {
        newLine.setAlignment(text.getAlignment());
        newLine.setUnparsedText(text.getUnparsedText().orElse(null));
        newLine.setLineWidth(text.getLineWidth());
        newLine.setShadowed(text.isShadowed());
        newLine.setSeeThrough(text.isSeeThrough());
        newLine.setBackgroundColor(text.getBackgroundColor().orElse(null));
        newLine.setTextOpacity(text.getTextOpacity());
        copyFromStatic(text, newLine);
    }

    private void copyFromBlock(final BlockHologramLine block, final BlockHologramLine newLine) {
        newLine.setBlock(block.getBlock());
        copyFromStatic(block, newLine);
    }

    private void copyFromItem(final ItemHologramLine item, final ItemHologramLine newLine) {
        newLine.setItemStack(item.getItemStack());
        newLine.setPlayerHead(item.isPlayerHead());
        newLine.setItemDisplayTransform(item.getItemDisplayTransform());
        copyFromStatic(item, newLine);
    }

    private void copyFromEntity(final EntityHologramLine entity, final EntityHologramLine newLine) {
        newLine.setScale(entity.getScale());
        copyFromStatic(entity, newLine);
    }

    private void copyFromStatic(final StaticHologramLine old, final StaticHologramLine newLine) {
        copyFrom(old, newLine);

        newLine.setGlowing(old.isGlowing());
        newLine.setGlowColor(old.getGlowColor().orElse(null));
        newLine.setBillboard(old.getBillboard());
        newLine.setOffset(old.getOffset());

        if (!(old instanceof final DisplayHologramLine oldDisplay)) return;
        if (!(newLine instanceof final DisplayHologramLine newDisplay)) return;

        copyFromDisplay(oldDisplay, newDisplay);
    }

    private void copyFromDisplay(final DisplayHologramLine old, final DisplayHologramLine newLine) {
        newLine.setTransformation(old.getTransformation());
        newLine.setInterpolationDuration(old.getInterpolationDuration());
        newLine.setTeleportDuration(old.getTeleportDuration());
        newLine.setViewRange(old.getViewRange());
        newLine.setShadowRadius(old.getShadowRadius());
        newLine.setShadowStrength(old.getShadowStrength());
        newLine.setDisplayWidth(old.getDisplayWidth());
        newLine.setDisplayHeight(old.getDisplayHeight());
        newLine.setInterpolationDelay(old.getInterpolationDelay());
        newLine.setBrightness(old.getBrightness().orElse(null));
    }
}
