package net.thenextlvl.service.providers.groupmanager;

import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
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
    public CompletableFuture<Group> createGroup(String name, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerGroup(holder.createGroup(name)));
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name) {
        return CompletableFuture.completedFuture(getGroup(name).orElse(null));
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name, World world) {
        return CompletableFuture.completedFuture(getGroup(name, world).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player) {
        return CompletableFuture.completedFuture(getGroupHolder(player).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player, World world) {
        return CompletableFuture.completedFuture(getGroupHolder(player, world).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid) {
        return CompletableFuture.completedFuture(getGroupHolder(uuid).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid, World world) {
        return CompletableFuture.completedFuture(getGroupHolder(uuid, world).orElse(null));
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups() {
        return CompletableFuture.completedFuture(getGroups());
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(World world) {
        return CompletableFuture.completedFuture(getGroups(world));
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

    @Override
    public Optional<Group> getGroup(String name) {
        return Optional.ofNullable(GroupManager.getGlobalGroups().getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<Group> getGroup(String name, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return Optional.ofNullable(holder)
                .map(holder1 -> holder1.getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(OfflinePlayer player) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(OfflinePlayer player, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, uuid, null);
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getHolder(holder, uuid, null);
    }

    @Override
    public Set<Group> getGroups() {
        return groupManager.getServer().getWorlds().stream()
                .<Group>mapMulti((world, consumer) -> getGroups(world).forEach(consumer))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Group> getGroups(World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return Set.of();
        return holder.getGroups().values().stream()
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet());
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
