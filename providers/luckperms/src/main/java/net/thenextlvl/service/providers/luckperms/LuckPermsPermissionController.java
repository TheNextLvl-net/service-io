package net.thenextlvl.service.providers.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid, @Nullable final World world) {
        if (world == null) return loadPermissionHolder(uuid);
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final UUID uuid) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid))
                .map(user -> new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final UUID uuid, @Nullable final World world) {
        if (world == null) return getPermissionHolder(uuid);
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
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
