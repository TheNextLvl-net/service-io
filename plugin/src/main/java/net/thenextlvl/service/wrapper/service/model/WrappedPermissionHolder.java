package net.thenextlvl.service.wrapper.service.model;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class WrappedPermissionHolder implements PermissionHolder {
    private final @Nullable World world;
    private final Player holder;
    private final Permission permission;

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