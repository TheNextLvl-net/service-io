package net.thenextlvl.service.controller.permission;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.LuckPermsPermissionHolder;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class LuckPermsPermissionController implements PermissionController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final @Getter String name = "LuckPerms";

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid))
                .map(user -> new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }
}
