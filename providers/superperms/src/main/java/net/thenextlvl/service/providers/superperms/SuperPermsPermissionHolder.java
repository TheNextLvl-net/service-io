package net.thenextlvl.service.providers.superperms;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.permission.PermissionHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
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
}
