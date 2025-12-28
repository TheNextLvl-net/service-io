package net.thenextlvl.service.providers.superperms;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public record SuperPermsPermissionHolder(CommandSender sender) implements PermissionHolder {
    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return sender.getEffectivePermissions().stream()
                .collect(Collectors.toUnmodifiableMap(
                        PermissionAttachmentInfo::getPermission,
                        PermissionAttachmentInfo::getValue
                ));
    }

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
    public boolean setPermission(String permission, boolean value) {
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
