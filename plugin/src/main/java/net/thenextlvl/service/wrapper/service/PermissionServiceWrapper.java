package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedPermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class PermissionServiceWrapper implements PermissionController, Wrapper {
    private final Permission permission;
    private final Plugin provider;

    public PermissionServiceWrapper(Permission permission, Plugin provider) {
        this.permission = permission;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, @Nullable World world) {
        return CompletableFuture.completedFuture(new WrappedPermissionHolder(world, player, permission));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, @Nullable World world) {
        return loadPermissionHolder(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player, @Nullable World world) {
        return Optional.of(new WrappedPermissionHolder(world, player, permission));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, @Nullable World world) {
        return getPermissionHolder(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return permission.getName() + " Wrapper";
    }
}
