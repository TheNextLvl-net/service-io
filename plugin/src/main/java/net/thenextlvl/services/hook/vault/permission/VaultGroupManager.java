package net.thenextlvl.services.hook.vault.permission;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class VaultGroupManager extends Permission {
    private final String name = "GroupManager";
    private final GroupManager groupManager;

    public VaultGroupManager(Plugin plugin) {
        this.groupManager = JavaPlugin.getPlugin(GroupManager.class);
        this.plugin = plugin;
    }

    @Override
    public boolean playerHas(@Nullable String worldName, @Nullable String playerName, @Nullable String permission) {
        AnjoPermissionsHandler owh;
        if (worldName == null) owh = getGroupManager().getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else owh = getGroupManager().getWorldsHolder().getWorldPermissions(worldName);
        if (owh == null) return false;
        return owh.permission(playerName, permission);
    }

    private @Nullable User getUser(@Nullable String playerName, @Nullable String worldName) {
        OverloadedWorldHolder owh;
        if (worldName == null) owh = getGroupManager().getWorldsHolder().getWorldDataByPlayerName(playerName);
        else owh = getGroupManager().getWorldsHolder().getWorldData(worldName);
        if (owh == null) return null;
        return owh.getUser(playerName);
    }

    @Override
    public boolean playerAdd(@Nullable String worldName, @Nullable String playerName, @Nullable String permission) {
        if (playerName == null || worldName == null) return false;
        var user = getUser(playerName, worldName);
        if (user == null) return false;
        user.addPermission(permission);
        var player = Bukkit.getPlayer(playerName);
        if (player != null) GroupManager.getBukkitPermissions().updatePermissions(player);
        return true;
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        if (playerName == null || worldName == null) return false;
        var user = getUser(playerName, worldName);
        if (user == null) return false;
        user.removePermission(permission);
        var player = Bukkit.getPlayer(playerName);
        if (player != null) GroupManager.getBukkitPermissions().updatePermissions(player);
        return true;
    }

    private @NotNull OverloadedWorldHolder getWorldHolder(String worldName) {
        if (worldName == null) return getGroupManager().getWorldsHolder().getDefaultWorld();
        else return getGroupManager().getWorldsHolder().getWorldData(worldName);
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        var holder = getWorldHolder(worldName);
        var group = holder.getGroup(groupName);
        if (group == null) return false;
        return group.hasSamePermissionNode(permission);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        var holder = getWorldHolder(worldName);
        var group = holder.getGroup(groupName);
        if (group == null) return false;
        group.addPermission(permission);
        return true;
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        var holder = getWorldHolder(worldName);
        var group = holder.getGroup(groupName);
        if (group == null) return false;
        group.removePermission(permission);
        return true;
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        AnjoPermissionsHandler awh;
        if (worldName == null) awh = getGroupManager().getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else awh = getGroupManager().getWorldsHolder().getWorldPermissions(worldName);
        return awh != null && awh.inGroup(playerName, groupName);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        if (playerName == null || groupName == null) return false;
        OverloadedWorldHolder owh;
        if (worldName == null) owh = getGroupManager().getWorldsHolder().getWorldDataByPlayerName(playerName);
        else owh = getGroupManager().getWorldsHolder().getWorldData(worldName);
        if (owh == null) return false;
        var user = owh.getUser(playerName);
        if (user == null) return false;
        var group = owh.getGroup(groupName);
        if (group == null) return false;
        if (user.getGroup().equals(owh.getDefaultGroup())) user.setGroup(group);
        else if (group.getInherits().contains(user.getGroup().getName().toLowerCase())) user.setGroup(group);
        else user.addSubGroup(group);
        var player = Bukkit.getPlayer(playerName);
        if (player != null) GroupManager.getBukkitPermissions().updatePermissions(player);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        if (playerName == null || groupName == null) return false;
        var worldHolder = getWorldHolder(worldName);
        var user = worldHolder.getUser(playerName);
        if (user == null) return false;
        var success = false;
        if (user.getGroup().getName().equalsIgnoreCase(groupName)) {
            user.setGroup(worldHolder.getDefaultGroup());
            success = true;
        } else {
            var group = worldHolder.getGroup(groupName);
            if (group != null) success = user.removeSubGroup(group);
        }
        if (success) {
            var player = Bukkit.getPlayer(playerName);
            if (player != null) GroupManager.getBukkitPermissions().updatePermissions(player);
        }
        return success;
    }

    @Override
    public String[] getPlayerGroups(String worldName, String playerName) {
        if (playerName == null) return new String[0];
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = getGroupManager().getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = getGroupManager().getWorldsHolder().getWorldPermissions(worldName);
        }
        return handler != null ? handler.getGroups(playerName) : new String[0];
    }

    @Override
    public @Nullable String getPrimaryGroup(String worldName, String playerName) {
        if (playerName == null) return null;
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = getGroupManager().getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = getGroupManager().getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) return null;
        return handler.getGroup(playerName);
    }

    @Override
    public String[] getGroups() {
        return plugin.getServer().getWorlds().stream()
                .map(WorldInfo::getName)
                .map(getGroupManager().getWorldsHolder()::getWorldData)
                .filter(Objects::nonNull)
                .map(OverloadedWorldHolder::getGroupList)
                .filter(Objects::nonNull)
                .<String>mapMulti((groups, consumer) -> groups.stream()
                        .map(Group::getName).forEach(consumer))
                .toList().toArray(new String[]{});
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getGroupManager().isEnabled();
    }
}