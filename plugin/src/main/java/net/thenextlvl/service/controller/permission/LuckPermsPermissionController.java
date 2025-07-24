package net.thenextlvl.service.controller.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.LuckPermsPermissionHolder;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class LuckPermsPermissionController implements PermissionController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Plugin plugin;

    public LuckPermsPermissionController(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, @Nullable World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            return new LuckPermsPermissionHolder(user, getOptions(world));
        });
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, @Nullable World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            return new LuckPermsPermissionHolder(user, getOptions(world));
        });
    }

    private QueryOptions getOptions(@Nullable World world) {
        if (world == null) return QueryOptions.defaultContextualOptions();
        return QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
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
