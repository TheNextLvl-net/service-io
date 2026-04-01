package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.Controller;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

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
