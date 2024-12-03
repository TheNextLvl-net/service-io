package net.thenextlvl.service.wrapper.service;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.wrapper.service.model.WrappedPermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
        return CompletableFuture.completedFuture(getPermissionHolder(player).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, World world) {
        return CompletableFuture.completedFuture(getPermissionHolder(player, world).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid) {
        return CompletableFuture.completedFuture(getPermissionHolder(uuid).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world) {
        return CompletableFuture.completedFuture(getPermissionHolder(uuid, world).orElse(null));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player) {
        return getPermissionHolder(player.getPlayer(), null);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world) {
        return getPermissionHolder(player.getPlayer(), world);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid) {
        return getPermissionHolder(plugin.getServer().getPlayer(uuid), null);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(plugin.getServer().getPlayer(uuid), world);
    }

    private Optional<PermissionHolder> getPermissionHolder(@Nullable Player player, @Nullable World world) {
        return Optional.ofNullable(player).map(online -> new WrappedPermissionHolder(world, online, permission));
    }

    @Override
    public String getName() {
        return permission.getName();
    }
}
