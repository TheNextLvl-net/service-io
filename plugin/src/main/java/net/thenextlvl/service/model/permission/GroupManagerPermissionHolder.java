package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupHolder;
import org.anjocaido.groupmanager.data.User;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record GroupManagerPermissionHolder(User user) implements GroupHolder {
    @Override
    public TriState checkPermission(String permission) {
        return user().hasSamePermissionNode(permission) ? TriState.TRUE : TriState.NOT_SET;
    }

    @Override
    public boolean addPermission(String permission) {
        if (checkPermission(permission).equals(TriState.TRUE)) return false;
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
    public boolean removeInfoNode(String key, String value) {
        var infoNode = getInfoNode(key).orElse(null);
        if (!value.equals(infoNode)) return false;
        return removeInfoNode(key);
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
        return Set.of();
    }

    @Override
    public String getPrimaryGroup() {
        return "";
    }

    @Override
    public boolean addGroup(Group group) {
        return false;
    }

    @Override
    public boolean addGroup(String group) {
        return false;
    }

    @Override
    public boolean inGroup(Group group) {
        return false;
    }

    @Override
    public boolean inGroup(String group) {
        return false;
    }

    @Override
    public boolean removeGroup(Group group) {
        return false;
    }

    @Override
    public boolean removeGroup(String group) {
        return false;
    }

    @Override
    public boolean setPrimaryGroup(Group group) {
        return false;
    }

    @Override
    public boolean setPrimaryGroup(String group) {
        return false;
    }
}
