package net.thenextlvl.service.providers.superperms;

import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.DoNotWrap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@DoNotWrap
public final class SuperPermsPermission extends Permission {
    private final Plugin plugin;

    public SuperPermsPermission(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "SuperPerms";
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
    public boolean playerHas(@Nullable final String world, final String player, final String permission) {
        return getPlayer(player).map(p -> p.hasPermission(permission)).orElse(false);
    }

    @Override
    public boolean playerAdd(@Nullable final String world, final String player, final String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(@Nullable final String world, final String player, final String permission) {
        return false;
    }

    @Override
    public boolean groupHas(@Nullable final String world, final String group, final String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(@Nullable final String world, final String group, final String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(@Nullable final String world, final String group, final String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(@Nullable final String world, final String player, final String group) {
        return playerHas(world, player, "groups." + group);
    }

    @Override
    public boolean playerAddGroup(@Nullable final String world, final String player, final String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(@Nullable final String world, final String player, final String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(@Nullable final String world, final String player) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(@Nullable final String world, final String player) {
        throw new UnsupportedOperationException(getName() + " has no group support");
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }

    private Optional<Player> getPlayer(final String player) {
        return Optional.ofNullable(plugin.getServer().getPlayer(player));
    }
}
