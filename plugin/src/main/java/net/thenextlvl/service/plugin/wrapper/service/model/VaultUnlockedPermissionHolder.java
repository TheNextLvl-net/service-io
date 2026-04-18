package net.thenextlvl.service.plugin.wrapper.service.model;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault2.permission.Permission;
import net.thenextlvl.service.permission.PermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public final class VaultUnlockedPermissionHolder implements PermissionHolder {
    private final @Nullable World world;
    private final OfflinePlayer holder;
    private final Permission permission;

    public VaultUnlockedPermissionHolder(@Nullable final World world, final OfflinePlayer holder, final Permission permission) {
        this.world = world;
        this.holder = holder;
        this.permission = permission;
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        final var player = holder.getPlayer();
        return player == null ? Map.of()
                : player.getEffectivePermissions().stream()
                  .collect(Collectors.toUnmodifiableMap(
                          PermissionAttachmentInfo::getPermission,
                          PermissionAttachmentInfo::getValue
                  ));
    }

    @Override
    public TriState checkPermission(final String permission) {
        return TriState.byBoolean(this.permission.playerHas(world != null ? world.getName() : null, holder, permission));
    }

    @Override
    public boolean addPermission(final String permission) {
        return this.permission.playerAdd(world != null ? world.getName() : null, holder, permission);
    }

    @Override
    public boolean removePermission(final String permission) {
        return this.permission.playerRemove(world != null ? world.getName() : null, holder, permission);
    }

    @Override
    public boolean setPermission(final String permission, final boolean value) {
        return false;
    }
}
