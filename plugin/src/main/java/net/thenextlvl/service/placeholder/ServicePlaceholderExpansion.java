package net.thenextlvl.service.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thenextlvl.service.ServicePlugin;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@NullMarked
public abstract class ServicePlaceholderExpansion<T> extends PlaceholderExpansion {
    private final Map<Pattern, PlaceholderResolver> resolvers = new HashMap<>();
    private final String identifier;
    private final @Nullable T provider;

    protected final ServicePlugin plugin;

    protected ServicePlaceholderExpansion(ServicePlugin plugin, String identifier, Class<T> providerClass) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.provider = plugin.getServer().getServicesManager().load(providerClass);
        if (provider == null) return;
        registerResolvers(provider);
        var name = provider.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        plugin.getComponentLogger().info("Registered placeholder expansion for '{}:{}'", identifier, name);
    }

    protected ServicePlaceholderExpansion(ServicePlugin plugin, Class<T> providerClass) {
        this(plugin, "serviceio", providerClass);
    }

    @Override
    public boolean canRegister() {
        return provider != null && super.canRegister();
    }

    @Override
    public final String getIdentifier() {
        return identifier;
    }

    @Override
    public final String getAuthor() {
        return String.join(", ", getAuthors());
    }

    protected List<String> getAuthors() {
        return plugin.getPluginMeta().getAuthors();
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public final boolean persist() {
        return true;
    }

    @Override
    public final @Nullable String onRequest(@Nullable OfflinePlayer player, String params) {
        try {
            if (player == null || provider == null) return null;
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

    protected final void registerResolver(Pattern pattern, PlaceholderResolver resolver) {
        resolvers.put(pattern, resolver);
    }

    protected final void registerResolver(String regex, PlaceholderResolver resolver) {
        resolvers.put(Pattern.compile(regex.replace("%s", "([^{}_]+)")), resolver);
    }

    protected abstract void registerResolvers(T provider);
}
