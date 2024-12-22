package net.thenextlvl.service.wrapper.service;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.wrapper.service.model.WrappedPermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
@RequiredArgsConstructor
public class PermissionServiceWrapper implements PermissionController {
    private final Permission permission;
    private final ServicePlugin plugin;

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player) {
        return CompletableFuture.completedFuture(new WrappedPermissionHolder(null, player, permission));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, World world) {
        return CompletableFuture.completedFuture(new WrappedPermissionHolder(world, player, permission));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid) {
        return loadPermissionHolder(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world) {
        return loadPermissionHolder(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player) {
        return Optional.of(new WrappedPermissionHolder(null, player, permission));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world) {
        return Optional.of(new WrappedPermissionHolder(world, player, permission));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid) {
        return getPermissionHolder(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public String getName() {
        return permission.getName();
    }
}
