package net.thenextlvl.service.controller.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.SuperPermsPermissionHolder;
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
public class SuperPermsPermissionController implements PermissionController {
    private final @Getter String name = "SuperPerms";
    private final ServicePlugin plugin;

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player) {
        return CompletableFuture.completedFuture(getPermissionHolder(player.getPlayer()).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, World world) {
        return loadPermissionHolder(player);
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid) {
        return CompletableFuture.completedFuture(getPermissionHolder(uuid).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world) {
        return loadPermissionHolder(uuid);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid) {
        return getPermissionHolder(plugin.getServer().getPlayer(uuid));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(uuid);
    }

    private Optional<PermissionHolder> getPermissionHolder(@Nullable Player player) {
        return Optional.ofNullable(player).map(SuperPermsPermissionHolder::new);
    }
}
