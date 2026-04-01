package net.thenextlvl.service.providers.superperms;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.permission.PermissionHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    public TriState checkPermission(final String permission) {
        return sender.permissionValue(permission);
    }

    @Override
    public boolean addPermission(final String permission) {
        return false;
    }

    @Override
    public boolean removePermission(final String permission) {
        return false;
    }

    @Override
    public boolean setPermission(final String permission, final boolean value) {
        return false;
    }

    @Override
    public <T> Optional<T> getInfoNode(final String key, final Function<@Nullable String, @Nullable T> mapper) {
        return Optional.empty();
    }

    @Override
    public boolean removeInfoNode(final String key) {
        return false;
    }

    @Override
    public boolean removeInfoNode(final String key, final String value) {
        return false;
    }

    @Override
    public boolean setInfoNode(final String key, final String value) {
        return false;
    }
}
