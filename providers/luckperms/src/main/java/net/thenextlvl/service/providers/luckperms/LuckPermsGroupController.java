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
public final class LuckPermsGroupController implements GroupController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Plugin plugin;

    public LuckPermsGroupController(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Group> createGroup(final String name) {
        return luckPerms.getGroupManager().createAndLoadGroup(name)
                .thenApply(group -> new LuckPermsGroup(group, group.getQueryOptions(), null));
    }

    @Override
    public CompletableFuture<Group> createGroup(final String name, final World world) {
        return luckPerms.getGroupManager().createAndLoadGroup(name).thenApply(group -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsGroup(group, context, null);
        });
    }

    @Override
    public CompletableFuture<Group> loadGroup(final String name) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional ->
                optional.<Group>map(group -> new LuckPermsGroup(group, group.getQueryOptions(), null))
                        .map(CompletableFuture::completedFuture)
                        .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<Group> loadGroup(final String name, final World world) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional -> optional.<Group>map(group -> {
                    final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
                    return new LuckPermsGroup(group, context, world);
                })
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(final UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(final UUID uuid, final World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            final var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, options);
        });
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups() {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(unused -> getGroups());
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(final World world) {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(unused -> getGroups(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(final Group group, final World world) {
        return !(group instanceof final LuckPermsGroup luckPermsGroup) ? deleteGroup(group.getName())
                : luckPerms.getGroupManager().deleteGroup(luckPermsGroup.group()).thenApply(none -> true);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(final String name) {
        return luckPerms.getGroupManager().loadGroup(name).thenApply(optional ->
                optional.map(luckPerms.getGroupManager()::deleteGroup).isPresent());
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(final String name, final World world) {
        return deleteGroup(name);
    }

    @Override
    public Optional<Group> getGroup(final String name) {
        return Optional.ofNullable(luckPerms.getGroupManager().getGroup(name)).map(group ->
                new LuckPermsGroup(group, group.getQueryOptions(), null));
    }

    @Override
    public Optional<Group> getGroup(final String name, final World world) {
        return Optional.ofNullable(luckPerms.getGroupManager().getGroup(name)).map(group -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsGroup(group, context, null);
        });
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(final UUID uuid) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(final UUID uuid, final World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
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
    public Set<Group> getGroups(final World world) {
        final var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
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
