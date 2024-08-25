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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class SuperPermsPermissionController implements PermissionController {
    private final @Getter String name = "SuperPerms";
    private final ServicePlugin plugin;

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
        return getPermissionHolder(plugin.getServer().getPlayer(uuid));
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(plugin.getServer().getPlayer(uuid));
    }

    private CompletableFuture<PermissionHolder> getPermissionHolder(@Nullable Player player) {
        return player == null ? CompletableFuture.completedFuture(null)
                : CompletableFuture.completedFuture(new SuperPermsPermissionHolder(player));
    }
}
