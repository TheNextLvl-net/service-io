package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public abstract class Converter<C extends Controller> {
    protected final Plugin plugin;
    protected final C source;
    protected final C target;

    protected Converter(final Plugin plugin, final C source, final C target) {
        this.plugin = plugin;
        this.source = source;
        this.target = target;
    }

    public abstract CompletableFuture<Void> convert();
}
