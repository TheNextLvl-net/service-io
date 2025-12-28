package net.thenextlvl.service.providers.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public class LuckPermsGroupController implements GroupController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Plugin plugin;

    public LuckPermsGroupController(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Group> createGroup(String name) {
        return luckPerms.getGroupManager().createAndLoadGroup(name)
                .thenApply(group -> new LuckPermsGroup(group, group.getQueryOptions(), null));
    }

    @Override
    public CompletableFuture<Group> createGroup(String name, World world) {
        return luckPerms.getGroupManager().createAndLoadGroup(name).thenApply(group -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsGroup(group, context, null);
        });
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional ->
                optional.<Group>map(group -> new LuckPermsGroup(group, group.getQueryOptions(), null))
                        .map(CompletableFuture::completedFuture)
                        .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name, World world) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional -> optional.<Group>map(group -> {
                    var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
                    return new LuckPermsGroup(group, context, world);
                })
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid, World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, options);
        });
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups() {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(unused -> getGroups());
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(World world) {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(unused -> getGroups(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group) {
        return !(group instanceof LuckPermsGroup luckPermsGroup) ? deleteGroup(group.getName())
                : luckPerms.getGroupManager().deleteGroup(luckPermsGroup.group()).thenApply(none -> true);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group, World world) {
        return deleteGroup(group);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name) {
        return luckPerms.getGroupManager().loadGroup(name).thenApply(optional ->
                optional.map(luckPerms.getGroupManager()::deleteGroup).isPresent());
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name, World world) {
        return deleteGroup(name);
    }

    @Override
    public Optional<Group> getGroup(String name) {
        return Optional.ofNullable(luckPerms.getGroupManager().getGroup(name)).map(group ->
                new LuckPermsGroup(group, group.getQueryOptions(), null));
    }

    @Override
    public Optional<Group> getGroup(String name, World world) {
        return Optional.ofNullable(luckPerms.getGroupManager().getGroup(name)).map(group -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsGroup(group, context, null);
        });
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid, World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, context);
        });
    }

    @Override
    public Set<Group> getGroups() {
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(group -> new LuckPermsGroup(group, group.getQueryOptions(), null))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Group> getGroups(World world) {
        var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(group -> new LuckPermsGroup(group, context, world))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return "LuckPerms Groups";
    }
}
