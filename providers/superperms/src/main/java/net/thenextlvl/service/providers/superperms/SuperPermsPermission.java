package net.thenextlvl.service.providers.superperms;

import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.Wrappable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SuperPermsPermission extends Permission implements Wrappable {
    private final Plugin plugin;

    public SuperPermsPermission(Plugin plugin) {
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
    public boolean playerHas(@Nullable String world, String player, String permission) {
        return getPlayer(player).map(p -> p.hasPermission(permission)).orElse(false);
    }

    @Override
    public boolean playerAdd(@Nullable String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(@Nullable String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(@Nullable String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(@Nullable String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(@Nullable String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(@Nullable String world, String player, String group) {
		return playerHas(world, player, "groups." + group);
    }

    @Override
    public boolean playerAddGroup(@Nullable String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(@Nullable String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(@Nullable String world, String player) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(@Nullable String world, String player) {
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

    private Optional<Player> getPlayer(String player) {
        return Optional.ofNullable(plugin.getServer().getPlayer(player));
    }

    @Override
    public boolean createWrapper() {
        return false;
    }
}
