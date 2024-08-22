package net.thenextlvl.service.vault.permission;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class VaultSuperPerms extends Permission {
    private final @NotNull String name = "SuperPerms";

    public VaultSuperPerms(@NotNull ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(@Nullable String world, @NotNull String name, @NotNull String permission) {
        var player = plugin.getServer().getPlayer(name);
        return player != null && player.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(@Nullable String world, @NotNull String player, @NotNull String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(@Nullable String world, @NotNull String player, @NotNull String permission) {
        return false;
    }

    @Override
    public boolean groupHas(@Nullable String world, @NotNull String group, @NotNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean groupAdd(@Nullable String world, @NotNull String group, @NotNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean groupRemove(@Nullable String world, @NotNull String group, @NotNull String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean playerInGroup(@Nullable String world, @NotNull String player, @NotNull String group) {
        return playerHas(world, player, "groups." + group);
    }

    @Override
    public boolean playerAddGroup(@Nullable String world, @NotNull String player, @NotNull String group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean playerRemoveGroup(@Nullable String world, @NotNull String player, @NotNull String group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String[] getPlayerGroups(@Nullable String world, @NotNull String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String getPrimaryGroup(@Nullable String world, @NotNull String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}
