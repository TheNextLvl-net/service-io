package net.thenextlvl.service.providers.groupmanager;

import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public final class GroupManagerGroupController implements GroupController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<Group> createGroup(final String name) {
        final var group = GroupManager.getGlobalGroups().newGroup(new org.anjocaido.groupmanager.data.Group(name));
        if (group != null) return CompletableFuture.completedFuture(new GroupManagerGroup(group));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Group> createGroup(final String name, @Nullable final World world) {
        if (world == null) return createGroup(name);
        final var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerGroup(holder.createGroup(name)));
    }

    @Override
    public CompletableFuture<Group> loadGroup(final String name) {
        return loadGroup(name, null);
    }

    @Override
    public CompletableFuture<Group> loadGroup(final String name, @Nullable final World world) {
        return CompletableFuture.completedFuture(getGroup(name, world).orElse(null));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(final OfflinePlayer player) {
        return loadGroupHolder(player, null);
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(final OfflinePlayer player, @Nullable final World world) {
        return CompletableFuture.completedFuture(getGroupHolder(player, world).orElse(null));
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Group>> loadGroups() {
        return loadGroups(null);
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(@Nullable final World world) {
        return CompletableFuture.completedFuture(getGroups(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(final String name) {
        return CompletableFuture.completedFuture(GroupManager.getGlobalGroups().removeGroup(name));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(final String name, final World world) {
        final var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return CompletableFuture.completedFuture(holder != null && holder.removeGroup(name));
    }

    @Override
    public Optional<Group> getGroup(final String name) {
        return Optional.ofNullable(GroupManager.getGlobalGroups().getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<Group> getGroup(final String name, @Nullable final World world) {
        final var holder = world != null
                ? groupManager.getWorldsHolder().getWorldData(world.getName())
                : groupManager.getWorldsHolder().getDefaultWorld();
        return Optional.ofNullable(holder)
                .map(holder1 -> holder1.getGroup(name))
                .map(GroupManagerGroup::new);
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(final OfflinePlayer player) {
        return getGroupHolder(player, null);
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(final OfflinePlayer player, @Nullable final World world) {
        final var holder = world != null
                ? groupManager.getWorldsHolder().getWorldData(world.getName())
                : groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public Set<Group> getGroups() {
        return groupManager.getServer().getWorlds().stream()
                .<Group>mapMulti((world, consumer) -> getGroups(world).forEach(consumer))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Group> getGroups(@Nullable final World world) {
        if (world == null) return getGroups();
        final var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        if (holder == null) return Set.of();
        return holder.getGroups().values().stream()
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Optional<GroupHolder> getHolder(@Nullable final WorldDataHolder holder, final UUID uuid, @Nullable final String name) {
        if (holder == null) return Optional.empty();
        final var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
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
