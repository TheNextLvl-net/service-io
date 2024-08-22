package net.thenextlvl.service.vault.chat;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class GroupManagerVaultChat extends Chat {
    private final @NotNull String name = "GroupManager - Chat";
    private final @NotNull ServicePlugin plugin;
    private final @NotNull GroupManager groupManager;

    public GroupManagerVaultChat(@NotNull ServicePlugin plugin, @NotNull Permission permission) {
        super(permission);
        this.groupManager = JavaPlugin.getPlugin(GroupManager.class);
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return groupManager.isEnabled();
    }

    @Override
    public int getPlayerInfoInteger(@Nullable String worldName, @Nullable String playerName, @Nullable String node, int defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getUserPermissionInteger(playerName, node);
    }

    @Override
    public void setPlayerInfoInteger(@Nullable String worldName, @Nullable String playerName, @Nullable String node, int value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String worldName, String groupName, String node, int defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getGroupPermissionInteger(groupName, node);
    }

    @Override
    public void setGroupInfoInteger(String worldName, String groupName, String node, int value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String worldName, String playerName, String node, double defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getUserPermissionDouble(playerName, node);
    }

    @Override
    public void setPlayerInfoDouble(String worldName, String playerName, String node, double value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public double getGroupInfoDouble(String worldName, String groupName, String node, double defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getGroupPermissionDouble(groupName, node);
    }

    @Override
    public void setGroupInfoDouble(String worldName, String groupName, String node, double value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String worldName, String playerName, String node, boolean defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getUserPermissionBoolean(playerName, node);
    }

    @Override
    public void setPlayerInfoBoolean(String worldName, String playerName, String node, boolean value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String worldName, String groupName, String node, boolean defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        return handler.getGroupPermissionBoolean(groupName, node);
    }

    @Override
    public void setGroupInfoBoolean(String worldName, String groupName, String node, boolean value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public @Nullable String getPlayerInfoString(String worldName, String playerName, String node, String defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        String val = handler.getUserPermissionString(playerName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoString(String worldName, String playerName, String node, String value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public @Nullable String getGroupInfoString(@Nullable String worldName, @Nullable String groupName, @Nullable String node, @Nullable String defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return defaultValue;
        String val = handler.getGroupPermissionString(groupName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoString(@Nullable String worldName, @Nullable String groupName, @Nullable String node, @Nullable String value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public @Nullable String getPlayerPrefix(@Nullable String worldName, @Nullable String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return "";
        return handler.getUserPrefix(playerName);
    }

    @Override
    public @Nullable String getPlayerSuffix(@Nullable String worldName, @Nullable String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        else handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        if (handler == null) return "";
        return handler.getUserSuffix(playerName);
    }

    @Override
    public void setPlayerSuffix(@Nullable String worldName, @Nullable String player, @Nullable String suffix) {
        setPlayerInfoString(worldName, player, "suffix", suffix);
    }

    @Override
    public void setPlayerPrefix(@Nullable String worldName, @Nullable String player, @Nullable String prefix) {
        setPlayerInfoString(worldName, player, "prefix", prefix);
    }

    @Override
    public @Nullable String getGroupPrefix(@Nullable String worldName, @Nullable String group) {
        return getGroupInfoString(worldName, group, "prefix", "");
    }

    @Override
    public void setGroupPrefix(@Nullable String worldName, @Nullable String group, @Nullable String prefix) {
        setGroupInfoString(worldName, group, "prefix", prefix);
    }

    @Override
    public @Nullable String getGroupSuffix(@Nullable String worldName, @Nullable String group) {
        return getGroupInfoString(worldName, group, "suffix", "");
    }

    @Override
    public void setGroupSuffix(@Nullable String worldName, @Nullable String group, @Nullable String suffix) {
        setGroupInfoString(worldName, group, "suffix", suffix);
    }

    private void setPlayerValue(@Nullable String worldName, @Nullable String playerName, @Nullable String node, @Nullable Object value) {
        OverloadedWorldHolder owh;
        if (worldName == null) owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        else owh = groupManager.getWorldsHolder().getWorldData(worldName);
        if (owh == null) return;
        var user = owh.getUser(playerName);
        if (user == null) return;
        user.getVariables().addVar(node, value);
    }

    private void setGroupValue(@Nullable String worldName, @Nullable String groupName, @Nullable String node, @Nullable Object value) {
        OverloadedWorldHolder owh;
        if (worldName == null) owh = groupManager.getWorldsHolder().getDefaultWorld();
        else owh = groupManager.getWorldsHolder().getWorldData(worldName);
        if (owh == null) return;
        var group = owh.getGroup(groupName);
        if (group == null) return;
        group.getVariables().addVar(node, value);
    }
}
