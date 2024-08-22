package net.thenextlvl.service.controller.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.LuckPermsPermissionHolder;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsPermissionController implements PermissionController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uniqueId) {
        return luckPerms.getUserManager().loadUser(uniqueId).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uniqueId, World world) {
        return luckPerms.getUserManager().loadUser(uniqueId).thenApply(user -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }
}
