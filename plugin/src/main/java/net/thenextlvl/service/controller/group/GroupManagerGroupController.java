package net.thenextlvl.service.controller.group;

import lombok.Getter;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.GroupManagerGroup;
import net.thenextlvl.service.model.permission.GroupManagerPermissionHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GroupManagerGroupController implements GroupController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);
    private final @Getter String name = "GroupManager";

    @Override
    public CompletableFuture<Group> createGroup(String name) {
        var group = GroupManager.getGlobalGroups().newGroup(new org.anjocaido.groupmanager.data.Group(name));
        if (group != null) return CompletableFuture.completedFuture(new GroupManagerGroup(group));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Group> createGroup(String name, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerGroup(holder.createGroup(name)));
    }

    @Override
    public CompletableFuture<Group> getGroup(String name) {
        return CompletableFuture.completedFuture(GroupManager.getGlobalGroups().getGroup(name))
                .thenApply(GroupManagerGroup::new);
    }

    @Override
    public CompletableFuture<Group> getGroup(String name, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(holder.getGroup(name))
                .thenApply(GroupManagerGroup::new);
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(OfflinePlayer player) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(OfflinePlayer player, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, uuid, null);
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, uuid, null);
    }

    private CompletableFuture<GroupHolder> getHolder(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return CompletableFuture.completedFuture(null);
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        if (user == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerPermissionHolder(user));
    }

    @Override
    public CompletableFuture<Set<Group>> getGroups() {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        if (holder == null) return CompletableFuture.completedFuture(Set.of());
        return CompletableFuture.completedFuture(holder.getGroups().values().stream()
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public CompletableFuture<Set<Group>> getGroups(World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(Set.of());
        return CompletableFuture.completedFuture(holder.getGroups().values().stream()
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group) {
        return deleteGroup(group.getName());
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group, World world) {
        return deleteGroup(group.getName(), world);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name) {
        return CompletableFuture.completedFuture(GroupManager.getGlobalGroups().removeGroup(name));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder != null) CompletableFuture.completedFuture(holder.removeGroup(name));
        return CompletableFuture.completedFuture(null);
    }
}
