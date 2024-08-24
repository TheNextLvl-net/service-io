package net.thenextlvl.service.controller.group;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.LuckPermsGroup;
import net.thenextlvl.service.model.permission.LuckPermsPermissionHolder;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsGroupController implements GroupController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final @Getter String name = "LuckPerms";

    @Override
    public CompletableFuture<Group> createGroup(String name) {
        return luckPerms.getGroupManager().createAndLoadGroup(name)
                .thenApply(group -> new LuckPermsGroup(group, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<Group> createGroup(String name, World world) {
        return luckPerms.getGroupManager().createAndLoadGroup(name).thenApply(group -> {
            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsGroup(group, context);
        });
    }

    @Override
    public CompletableFuture<Group> getGroup(String name) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional ->
                optional.<Group>map(group -> new LuckPermsGroup(group, QueryOptions.defaultContextualOptions()))
                        .map(CompletableFuture::completedFuture)
                        .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<Group> getGroup(String name, World world) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional -> optional.<Group>map(group -> {
                    var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
                    return new LuckPermsGroup(group, context);
                })
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsPermissionHolder(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<GroupHolder> getGroupHolder(UUID uuid, World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsPermissionHolder(user, options);
        });
    }

    @Override
    public CompletableFuture<Set<Group>> getGroups() {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(none ->
                luckPerms.getGroupManager().getLoadedGroups().stream()
                        .map(group -> new LuckPermsGroup(group, QueryOptions.defaultContextualOptions()))
                        .collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public CompletableFuture<Set<Group>> getGroups(World world) {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(none ->
                luckPerms.getGroupManager().getLoadedGroups().stream()
                        .map(group -> {
                            var context = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
                            return new LuckPermsGroup(group, context);
                        })
                        .collect(Collectors.toUnmodifiableSet()));
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
}
