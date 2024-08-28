package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.function.Function;

public record SuperPermsPermissionHolder(CommandSender sender) implements PermissionHolder {
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
}
