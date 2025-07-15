package net.thenextlvl.service.controller.group;

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
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    public CompletableFuture<Group> createGroup(String name, @Nullable World world) {
        return luckPerms.getGroupManager().createAndLoadGroup(name).thenApply(group -> {
            return new LuckPermsGroup(group, getOptions(world), null);
        });
    }

    @Override
    public CompletableFuture<Group> loadGroup(String name, @Nullable World world) {
        return luckPerms.getGroupManager().loadGroup(name).thenCompose(optional -> optional.<Group>map(group ->
                        new LuckPermsGroup(group, getOptions(world), world))
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.completedFuture(null)));
    }

    @Override
    public CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid, @Nullable World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            return new LuckPermsPermissionHolder(user, getOptions(world));
        });
    }

    @Override
    public CompletableFuture<Set<Group>> loadGroups(@Nullable World world) {
        return luckPerms.getGroupManager().loadAllGroups().thenApply(unused -> getGroups(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(Group group, @Nullable World world) {
        if (!(group instanceof LuckPermsGroup luckPermsGroup)) return CompletableFuture.completedFuture(false);
        return luckPerms.getGroupManager().deleteGroup(luckPermsGroup.group()).thenApply(none -> true);
    }

    @Override
    public CompletableFuture<Boolean> deleteGroup(String name, @Nullable World world) {
        return luckPerms.getGroupManager().loadGroup(name).thenApply(optional ->
                optional.map(luckPerms.getGroupManager()::deleteGroup).isPresent());
    }

    @Override
    public Optional<Group> getGroup(String name, @Nullable World world) {
        return Optional.ofNullable(luckPerms.getGroupManager().getGroup(name)).map(group -> {
            return new LuckPermsGroup(group, getOptions(world), null);
        });
    }

    @Override
    public Optional<GroupHolder> getGroupHolder(UUID uuid, @Nullable World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            return new LuckPermsPermissionHolder(user, getOptions(world));
        });
    }

    @Override
    public Set<Group> getGroups(@Nullable World world) {
        var options = getOptions(world);
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .map(group -> new LuckPermsGroup(group, options, world))
                .collect(Collectors.toUnmodifiableSet());
    }

    private QueryOptions getOptions(@Nullable World world) {
        if (world == null) return luckPerms.getContextManager().getStaticQueryOptions();
        return QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
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
