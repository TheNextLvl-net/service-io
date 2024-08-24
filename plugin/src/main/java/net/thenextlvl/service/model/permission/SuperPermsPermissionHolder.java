package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupHolder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public record SuperPermsPermissionHolder(CommandSender sender) implements GroupHolder {
    @Override
    public TriState checkPermission(String permission) {
        return sender.permissionValue(permission);
    }

    @Override
    public boolean addPermission(String permission) {
        return false;
    }

    @Override
    public boolean removePermission(String permission) {
        return false;
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<String, T> mapper) {
        return Optional.empty();
    }

    @Override
    public boolean removeInfoNode(String key) {
        return false;
    }

    @Override
    public boolean removeInfoNode(String key, String value) {
        return false;
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        return false;
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
