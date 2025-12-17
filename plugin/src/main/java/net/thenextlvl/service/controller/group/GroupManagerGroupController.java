package net.thenextlvl.service.controller.group;

import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.GroupManagerGroup;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public class GroupManagerGroupController implements GroupController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<Group> createGroup(String name) {
        var group = GroupManager.getGlobalGroups().newGroup(new org.anjocaido.groupmanager.data.Group(name));
        if (group != null) return CompletableFuture.completedFuture(new GroupManagerGroup(group));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Group> createGroup(String name, @Nullable World world) {
        if (world == null) return createGroup(name);
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerGroup(holder.createGroup(name)));
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name, @Nullable World world) {
        return CompletableFuture.completedFuture(getGroup(name, world).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid, @Nullable World world) {
        return CompletableFuture.completedFuture(getGroupHolder(uuid, world).orElse(null));
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(@Nullable World world) {
        return CompletableFuture.completedFuture(getGroups(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group, @Nullable World world) {
        return deleteGroup(group.getName(), world);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name) {
        return CompletableFuture.completedFuture(GroupManager.getGlobalGroups().removeGroup(name));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name, @Nullable World world) {
        if (world == null) return deleteGroup(name);
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder != null) CompletableFuture.completedFuture(holder.removeGroup(name));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Optional<Group> getGroup(String name) {
        return Optional.ofNullable(GroupManager.getGlobalGroups().getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<Group> getGroup(String name, @Nullable World world) {
        if (world == null) return getGroup(name);
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return Optional.ofNullable(holder)
                .map(holder1 -> holder1.getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid, @Nullable World world) {
        return getHolder(getHolder(world), uuid, null);
    }

    @Override
    public Set<Group> getGroups() {
        return groupManager.getServer().getWorlds().stream()
                .<Group>mapMulti((world, consumer) -> getGroups(world).forEach(consumer))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Group> getGroups(@Nullable World world) {
        if (world == null) return getGroups();
        var holder = getHolder(world);
        if (holder == null) return Set.of();
        return holder.getGroups().values().stream()
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private @Nullable OverloadedWorldHolder getHolder(@Nullable World world) {
        if (world == null) return groupManager.getWorldsHolder().getDefaultWorld();
        return groupManager.getWorldsHolder().getWorldData(world.getName());
    }

    private Optional<GroupHolder> getHolder(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return Optional.empty();
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        if (user == null) return Optional.empty();
        return Optional.of(new GroupManagerPermissionHolder(user, holder));
    }

    @Override
    public Plugin getPlugin() {
        return groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager Groups";
    }
}
