package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.LuckPermsGroup;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record LuckPermsPermissionHolder(User user, QueryOptions options) implements GroupHolder {
    @Override
    public TriState checkPermission(String permission) {
        return switch (user().getCachedData().getPermissionData(options()).checkPermission(permission)) {
            case FALSE -> TriState.FALSE;
            case TRUE -> TriState.TRUE;
            case UNDEFINED -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        var result = user().data().add(PermissionNode.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removePermission(String permission) {
        var result = user().data().add(PermissionNode.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<String, T> mapper) {
        return user().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        var result = user().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(String key) {
        user().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean removeInfoNode(String key, String value) {
        var result = user().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public Set<Group> getGroups() {
        return user().getInheritedGroups(options()).stream()
                .map(group -> new LuckPermsGroup(group, options()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPrimaryGroup() {
        return user().getPrimaryGroup();
    }

    @Override
    public boolean addGroup(Group group) {
        return addGroup(group.getName());
    }

    @Override
    public boolean addGroup(String name) {
        return addPermission("group." + name);
    }

    @Override
    public boolean inGroup(Group group) {
        return inGroup(group.getName());
    }

    @Override
    public boolean inGroup(String name) {
        return checkPermission("group." + name).equals(TriState.TRUE);
    }

    @Override
    public boolean removeGroup(Group group) {
        return removeGroup(group.getName());
    }

    @Override
    public boolean removeGroup(String name) {
        return removePermission("group." + name);
    }

    @Override
    public boolean setPrimaryGroup(Group group) {
        return setPrimaryGroup(group.getName());
    }

    @Override
    public boolean setPrimaryGroup(String name) {
        return user.setPrimaryGroup(name).wasSuccessful();
    }
}
