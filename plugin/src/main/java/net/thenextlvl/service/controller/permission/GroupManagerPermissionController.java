package net.thenextlvl.service.controller.permission;

import lombok.Getter;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.GroupManagerPermissionHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GroupManagerPermissionController implements PermissionController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);
    private final @Getter String name = "GroupManager";

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(OfflinePlayer player) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, uuid, null);
    }

    @Override
    public CompletableFuture<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, uuid, null);
    }

    private CompletableFuture<PermissionHolder> getHolder(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return CompletableFuture.completedFuture(null);
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        if (user == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerPermissionHolder(user));
    }
}
