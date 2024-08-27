package net.thenextlvl.service.controller.permission;

import lombok.Getter;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.GroupManagerPermissionHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GroupManagerPermissionController implements PermissionController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);
    private final @Getter String name = "GroupManager";

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return CompletableFuture.completedFuture(getHolder(holder, uuid).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return CompletableFuture.completedFuture(getHolder(holder, uuid).orElse(null));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world) {
        return Optional.empty();
    }

    private Optional<PermissionHolder> getHolder(@Nullable WorldDataHolder holder, UUID uuid) {
        return holder != null ? Optional.ofNullable(holder.getUser(uuid.toString()))
                .map(user -> new GroupManagerPermissionHolder(user, holder)) : Optional.empty();
    }
}
