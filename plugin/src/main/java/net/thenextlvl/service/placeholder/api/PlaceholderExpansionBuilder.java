package net.thenextlvl.service.placeholder.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.thenextlvl.service.ServicePlugin;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class PlaceholderExpansionBuilder extends PlaceholderExpansion {
    private final Set<PlaceholderStore<?>> stores = new HashSet<>();
    private final String identifier;

    private String author;
    private String version;

    protected final ServicePlugin plugin;

    public PlaceholderExpansionBuilder(ServicePlugin plugin, String identifier) {
        this.author = String.join(", ", plugin.getPluginMeta().getAuthors());
        this.version = plugin.getPluginMeta().getVersion();
        this.identifier = identifier;
        this.plugin = plugin;
    }

    public PlaceholderExpansionBuilder(ServicePlugin plugin) {
        this(plugin, "serviceio");
    }

    public final PlaceholderExpansionBuilder registerStore(PlaceholderStore<?> store) {
        if (store.isEnabled()) stores.add(store);
        return this;
    }

    public final PlaceholderExpansionBuilder setAuthors(Collection<String> authors) {
        this.author = String.join(", ", authors);
        return this;
    }

    public PlaceholderExpansionBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public boolean canRegister() {
        return !stores.isEmpty() && super.canRegister();
    }

    @Override
    public final String getIdentifier() {
        return identifier;
    }

    @Override
    public final String getAuthor() {
        return author;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public final boolean persist() {
        return true;
    }

    @Override
    public final @Nullable String onRequest(@Nullable OfflinePlayer player, String params) {
        return player == null ? null : stores.stream()
                .map(store -> store.resolve(player, params))
                .filter(Objects::nonNull)
                .findAny().orElse(null);
    }
}
