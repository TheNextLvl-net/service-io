package net.thenextlvl.service.providers.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DoNotWrap
@NullMarked
public final class LuckPermsPermissionController implements PermissionController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Plugin plugin;

    public LuckPermsPermissionController(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId()).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, final World world) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId()).thenApply(user -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId()))
                .map(user -> new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, final World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId())).map(user -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return "LuckPerms";
    }
}
