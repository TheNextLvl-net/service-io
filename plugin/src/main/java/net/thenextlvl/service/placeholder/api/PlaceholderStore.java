package net.thenextlvl.service.placeholder.api;

import net.thenextlvl.service.ServicePlugin;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@NullMarked
public abstract class PlaceholderStore<T> {
    private final Map<Pattern, PlaceholderResolver> resolvers = new HashMap<>();
    private final @Nullable T provider;
    
    protected final ServicePlugin plugin;

    public PlaceholderStore(ServicePlugin plugin, Class<T> providerClass) {
        this.plugin = plugin;
        this.provider = plugin.getServer().getServicesManager().load(providerClass);
        if (provider == null) return;
        registerResolvers(provider);
        plugin.getComponentLogger().info("Registered placeholders for {} ({})",
                provider.getClass().getSimpleName(), providerClass.getSimpleName());
    }

    public final void registerResolver(Pattern pattern, PlaceholderResolver resolver) {
        resolvers.put(pattern, resolver);
    }

    protected final void registerResolver(String regex, PlaceholderResolver resolver) {
        resolvers.put(Pattern.compile(regex.replace("%s", "([^{}_]+)")), resolver);
    }

    protected abstract void registerResolvers(T provider);

    public final @Nullable String resolve(OfflinePlayer player, String params) {
        try {
            for (var entry : resolvers.entrySet()) {
                var matcher = entry.getKey().matcher(params);
                if (!matcher.matches()) continue;
                return entry.getValue().resolve(player, matcher);
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
}
