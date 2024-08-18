package net.thenextlvl.services.hook.permission;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;

@Getter
public class VaultSuperPerms extends Permission {
    private final String name = "SuperPerms";

    public VaultSuperPerms(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String name, String permission) {
        var player = plugin.getServer().getPlayer(name);
        return player != null && player.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return playerHas(world, player, "groups." + group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}
