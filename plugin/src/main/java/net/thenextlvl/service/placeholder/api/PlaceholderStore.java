package net.thenextlvl.service.placeholder.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@NullMarked
public abstract class PlaceholderStore<T> implements Listener {
    private final Map<Pattern, PlaceholderResolver<T>> resolvers = new HashMap<>();
    private final Class<T> providerClass;
    private @Nullable T provider;

    protected final Plugin plugin;

    public PlaceholderStore(Plugin plugin, Class<T> providerClass) {
        this.plugin = plugin;
        this.providerClass = providerClass;
        updateServices();
        registerResolvers();
        plugin.getComponentLogger().info("Registered placeholders for {} ({})",
                providerClass.getSimpleName(), providerClass.getSimpleName());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void registerResolver(Pattern pattern, PlaceholderResolver<T> resolver) {
        resolvers.put(pattern, resolver);
    }

    protected final void registerResolver(String regex, PlaceholderResolver<T> resolver) {
        resolvers.put(Pattern.compile(regex.replace("%s", "([^{}]+)")), resolver);
    }

    protected abstract void registerResolvers();

    public final @Nullable String resolve(OfflinePlayer player, String params) {
        try {
            if (provider != null) for (var entry : resolvers.entrySet()) {
                var matcher = entry.getKey().matcher(params);
                if (!matcher.matches()) continue;
                var resolved = entry.getValue().resolve(provider, player, matcher);
                if (resolved != null) return resolved;
            }
            return null;
        } catch (Exception e) {
            var name = player.getName() != null ? player.getName() : player.getUniqueId().toString();
            plugin.getComponentLogger().warn("Failed to resolve placeholder '{}' for player {}", params, name, e);
            return null;
        }
    }

    public final boolean isEnabled() {
        return provider != null;
    }

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (providerClass.isInstance(event.getProvider().getProvider())) updateServices();
    }

    @EventHandler
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (providerClass.isInstance(event.getProvider().getProvider())) updateServices();
    }

    private void updateServices() {
        this.provider = plugin.getServer().getServicesManager().load(providerClass);
    }
}
