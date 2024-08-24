package net.thenextlvl.service.controller.permission;

import lombok.Getter;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.SuperPermsPermissionHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class SuperPermsPermissionController implements PermissionController {
    private final String name = "SuperPerms";

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(OfflinePlayer player) {
        return getPermissionHolder(player.getPlayer());
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world) {
        return getPermissionHolder(player.getPlayer());
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uuid) {
        return getPermissionHolder(Bukkit.getPlayer(uuid));
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(Bukkit.getPlayer(uuid));
    }

    private CompletableFuture<PermissionHolder> getPermissionHolder(@Nullable Player player) {
        return player == null ? CompletableFuture.completedFuture(null)
                : CompletableFuture.completedFuture(new SuperPermsPermissionHolder(player));
    }
}
