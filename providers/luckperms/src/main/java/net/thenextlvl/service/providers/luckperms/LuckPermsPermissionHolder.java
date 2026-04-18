package net.thenextlvl.service.providers.luckperms;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupHolder;
import net.thenextlvl.service.model.MetadataHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public record LuckPermsPermissionHolder(User user, QueryOptions options) implements GroupHolder, MetadataHolder {
    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return user().getCachedData().getPermissionData(options()).getPermissionMap();
    }

    @Override
    public TriState checkPermission(final String permission) {
        return switch (user().getCachedData().getPermissionData(options()).checkPermission(permission)) {
            case FALSE -> TriState.FALSE;
            case TRUE -> TriState.TRUE;
            case UNDEFINED -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(final String permission) {
        return setPermission(permission, true);
    }

    @Override
    public boolean removePermission(final String permission) {
        final var result = user().data().remove(Node.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean setPermission(final String permission, final boolean value) {
        final var result = user().data().add(Node.builder(permission).value(value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public <T> Optional<T> getInfoNode(final String key, final Function<@Nullable String, @Nullable T> mapper) {
        return user().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(final String key, final String value) {
        final var result = user().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(final String key) {
        user().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean removeInfoNode(final String key, final String value) {
        final var result = user().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public Set<Group> getGroups() {
        return user().getInheritedGroups(options()).stream()
                .map(group -> new LuckPermsGroup(group, options(), null))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPrimaryGroup() {
        return user().getPrimaryGroup();
    }

    @Override
    public boolean addGroup(final Group group) {
        return addGroup(group.getName());
    }

    @Override
    public boolean addGroup(final String name) {
        return addPermission("group." + name);
    }

    @Override
    public boolean inGroup(final Group group) {
        return inGroup(group.getName());
    }

    @Override
    public boolean inGroup(final String name) {
        return checkPermission("group." + name).equals(TriState.TRUE);
    }

    @Override
    public boolean removeGroup(final Group group) {
        return removeGroup(group.getName());
    }

    @Override
    public boolean removeGroup(final String name) {
        return removePermission("group." + name);
    }

    @Override
    public boolean setPrimaryGroup(final Group group) {
        return setPrimaryGroup(group.getName());
    }

    @Override
    public boolean setPrimaryGroup(final String name) {
        return user.setPrimaryGroup(name).wasSuccessful();
    }
}
