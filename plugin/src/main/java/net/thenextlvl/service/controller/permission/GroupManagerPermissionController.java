package net.thenextlvl.service.controller.permission;

import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.GroupManagerPermissionHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class GroupManagerPermissionController implements PermissionController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, @Nullable World world) {
        return CompletableFuture.completedFuture(getHolder(getHolder(world), uuid).orElse(null));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, @Nullable World world) {
        return getHolder(getHolder(world), uuid);
    }

    private @Nullable OverloadedWorldHolder getHolder(@Nullable World world) {
        if (world == null) return groupManager.getWorldsHolder().getDefaultWorld();
        return groupManager.getWorldsHolder().getWorldData(world.getName());
    }

    private Optional<PermissionHolder> getHolder(@Nullable WorldDataHolder holder, UUID uuid) {
        return holder != null ? Optional.ofNullable(holder.getUser(uuid.toString()))
                .map(user -> new GroupManagerPermissionHolder(user, holder)) : Optional.empty();
    }

    @Override
    public Plugin getPlugin() {
        return groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager";
    }
}
