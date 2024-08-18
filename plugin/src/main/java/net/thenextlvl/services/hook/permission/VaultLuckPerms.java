package net.thenextlvl.services.hook.permission;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.services.ServicePlugin;

import java.util.Objects;

@Getter
public class VaultLuckPerms extends Permission {
    private final String name = "LuckPerms";
    private final LuckPerms luckPerms;

    public VaultLuckPerms(ServicePlugin plugin) {
        var servicesManager = plugin.getServer().getServicesManager();
        this.luckPerms = Objects.requireNonNull(servicesManager.load(LuckPerms.class), "LuckPerms not found");
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
    public boolean playerHas(String world, String player, String permission) {
        var user = luckPerms.getUserManager().getUser(player);
        return user != null && user.getCachedData().getPermissionData(QueryOptions.builder(QueryMode.CONTEXTUAL)
                .context(ImmutableContextSet.of("world", world)).build()
        ).checkPermission(permission).asBoolean();
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
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return "";
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
