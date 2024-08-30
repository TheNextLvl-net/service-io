package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.model.group.GroupManagerGroup;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GroupManagerPermissionHolder(User user, WorldDataHolder holder) implements GroupHolder {
    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return user().getPermissionList().stream().collect(Collectors.toUnmodifiableMap(
                permission -> permission, permission -> checkPermission(permission).toBooleanOrElse(false))
        );
    }

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
        return setPermission(permission, true);
    }

    @Override
    public boolean removePermission(String permission) {
        return user().removePermission(permission);
    }

    @Override
    public boolean setPermission(String permission, boolean value) {
        var state = checkPermission(permission).toBoolean();
        if (state != null && state.equals(value)) return false;
        removePermission(value ? "-" + permission : permission);
        user().addPermission(!value ? "-" + permission : permission);
        return true;
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
        return holder.getGroups().values().stream()
                .filter(group -> inGroup(group.getName()))
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
        return handler.hasGroupInInheritance(user().getGroup(), name);
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
