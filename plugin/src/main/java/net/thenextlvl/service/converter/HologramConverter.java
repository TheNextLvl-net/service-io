package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.hologram.HologramController;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class HologramConverter implements Converter<HologramController> {
    @Override
    public CompletableFuture<Void> convert(final HologramController source, final HologramController target) {
        source.getHolograms().forEach(hologram -> {
            final var created = target.createHologram(hologram.getName(), hologram.getLocation(), hologram.getLines());
            created.addViewers(hologram.getViewers());
            created.setDisplayRange(hologram.getDisplayRange());
            created.setPersistent(hologram.isPersistent());
            created.setVisibleByDefault(hologram.isVisibleByDefault());
            created.persist();
        });
        return CompletableFuture.completedFuture(null);
    }
}
