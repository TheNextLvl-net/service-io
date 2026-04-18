package net.thenextlvl.service.plugin.wrapper.service;

import net.milkbowl.vault2.permission.Permission;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import net.thenextlvl.service.plugin.wrapper.service.model.VaultUnlockedPermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class VaultUnlockedPermissionServiceWrapper implements PermissionController, Wrapper {
    private final Permission permission;
    private final Plugin provider;

    public VaultUnlockedPermissionServiceWrapper(final Permission permission, final Plugin provider) {
        this.permission = permission;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player) {
        return loadPermissionHolder(player, null);
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        return CompletableFuture.completedFuture(new VaultUnlockedPermissionHolder(world, player, permission));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player) {
        return Optional.of(new VaultUnlockedPermissionHolder(null, player, permission));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        return Optional.of(new VaultUnlockedPermissionHolder(world, player, permission));
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
