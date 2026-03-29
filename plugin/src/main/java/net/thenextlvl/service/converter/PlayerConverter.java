package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@NullMarked
abstract class PlayerConverter<C extends Controller> extends Converter<C> {
    protected PlayerConverter(final Plugin plugin, final C source, final C target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        return CompletableFuture.allOf(Arrays.stream(plugin.getServer().getOfflinePlayers())
                .map(this::convert)
                .toArray(CompletableFuture[]::new));
    }

    public abstract CompletableFuture<Void> convert(OfflinePlayer player);
}
