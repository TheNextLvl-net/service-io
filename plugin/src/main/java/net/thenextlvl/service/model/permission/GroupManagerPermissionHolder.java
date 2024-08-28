package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.GroupManagerGroup;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GroupManagerPermissionHolder(User user, WorldDataHolder holder) implements GroupHolder {
    @Override
    public TriState checkPermission(String permission) {
        return switch (holder().getPermissionsHandler().checkFullUserPermission(user(), permission).resultType) {
            case FOUND -> TriState.TRUE;
            case NEGATION, EXCEPTION -> TriState.FALSE;
            default -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        if (!checkPermission(permission).equals(TriState.NOT_SET)) return false;
        user().addPermission(permission);
        return true;
    }

    @Override
    public boolean removePermission(String permission) {
        return user().removePermission(permission);
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper) {
        return Optional.ofNullable(mapper.apply(user().getVariables().getVarString(key)));
    }

    @Override
    public boolean removeInfoNode(String key) {
        if (!hasInfoNode(key)) return false;
        user().getVariables().removeVar(key);
        return true;
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        user().getVariables().addVar(key, value);
        return true;
    }

    @Override
    public boolean hasInfoNode(String key) {
        return user().getVariables().hasVar(key);
    }

    @Override
    public @Unmodifiable Set<Group> getGroups() {
        var groups = new HashSet<String>();
        var handler = holder().getPermissionsHandler();

        var inherited = handler.listAllGroupsInherited(user().getGroup());
        if (inherited != null) groups.addAll(inherited);

        user().subGroupListCopy().forEach(group -> groups.addAll(handler.listAllGroupsInherited(group)));

        return groups.stream().map(holder()::getGroup)
                .map(GroupManagerGroup::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPrimaryGroup() {
        return user().getGroupName();
    }

    @Override
    public boolean addGroup(Group group) {
        if (!(group instanceof GroupManagerGroup managerGroup))
            return addGroup(group.getName());
        return user().addSubGroup(managerGroup.group());
    }

    @Override
    public boolean addGroup(String name) {
        var group = holder().getGroup(name);
        return group != null && user().addSubGroup(group);
    }

    @Override
    public boolean inGroup(Group group) {
        return inGroup(group.getName());
    }

    @Override
    public boolean inGroup(String name) {
        var handler = holder().getPermissionsHandler();
        if (handler.hasGroupInInheritance(user().getGroup(), name)) return true;
        return user().subGroupListCopy().stream().anyMatch(group ->
                handler.hasGroupInInheritance(group, name));
    }

    @Override
    public boolean removeGroup(Group group) {
        if (!(group instanceof GroupManagerGroup managerGroup))
            return removeGroup(group.getName());
        return user().removeSubGroup(managerGroup.group());
    }

    @Override
    public boolean removeGroup(String name) {
        var group = holder().getGroup(name);
        return group != null && user().removeSubGroup(group);
    }

    @Override
    public boolean setPrimaryGroup(Group group) {
        if (!(group instanceof GroupManagerGroup managerGroup))
            return setPrimaryGroup(group.getName());
        user().setGroup(managerGroup.group(), true);
        return true;
    }

    @Override
    public boolean setPrimaryGroup(String name) {
        var group = holder().getGroup(name);
        if (group != null) user().setGroup(group, true);
        return group != null;
    }
}
