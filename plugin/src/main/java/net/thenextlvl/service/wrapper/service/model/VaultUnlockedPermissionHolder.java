package net.thenextlvl.service.wrapper.service.model;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault2.permission.Permission;
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

    @Override
    public <T> Optional<T> getInfoNode(final String key, final Function<@Nullable String, @Nullable T> mapper) {
        return Optional.empty();
    }

    @Override
    public boolean removeInfoNode(final String key) {
        return false;
    }

    @Override
    public boolean setInfoNode(final String key, final String value) {
        return false;
    }
}
