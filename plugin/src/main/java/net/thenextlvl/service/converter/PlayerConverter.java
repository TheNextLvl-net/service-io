package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@NullMarked
abstract class PlayerConverter<C extends Controller> implements Converter<C> {
    public abstract CompletableFuture<Void> convert(OfflinePlayer player, C source, C target);

    @Override
    public CompletableFuture<Void> convert(final C source, final C target) {
        return CompletableFuture.allOf(Arrays.stream(source.getPlugin().getServer().getOfflinePlayers())
                .map(player -> convert(player, source, target))
                .toArray(CompletableFuture[]::new));
    }
}
