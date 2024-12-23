package net.thenextlvl.service.wrapper.service.model;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public class WrappedPermissionHolder implements PermissionHolder {
    private final @Nullable World world;
    private final OfflinePlayer holder;
    private final Permission permission;

    public WrappedPermissionHolder(@Nullable World world, OfflinePlayer holder, Permission permission) {
        this.world = world;
        this.holder = holder;
        this.permission = permission;
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        var player = holder.getPlayer();
        return player != null ? player.getEffectivePermissions().stream()
                .collect(Collectors.toUnmodifiableMap(
                        PermissionAttachmentInfo::getPermission,
                        PermissionAttachmentInfo::getValue
                )) : Map.of();
    }

    @Override
    public TriState checkPermission(String permission) {
        return TriState.byBoolean(this.permission.playerHas(world != null ? world.getName() : null, holder, permission));
    }

    @Override
    public boolean addPermission(String permission) {
        return this.permission.playerAdd(world != null ? world.getName() : null, holder, permission);
    }

    @Override
    public boolean removePermission(String permission) {
        return this.permission.playerRemove(world != null ? world.getName() : null, holder, permission);
    }

    @Override
    public boolean setPermission(String permission, boolean value) {
        return false;
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper) {
        return Optional.empty();
    }

    @Override
    public boolean removeInfoNode(String key) {
        return false;
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        return false;
    }
}