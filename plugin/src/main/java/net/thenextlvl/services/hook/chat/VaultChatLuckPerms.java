package net.thenextlvl.services.hook.chat;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.services.ServicePlugin;

import java.util.Optional;

@Getter
public class VaultChatLuckPerms extends Chat {
    private final String name = "LuckPerms - Chat";
    private final LuckPerms luckPerms;

    public VaultChatLuckPerms(ServicePlugin plugin, Permission permission) {
        super(permission);
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        if (world == null || player == null) return "";
        var user = luckPerms.getUserManager().getUser(player);
        if (user == null) return "";
        var options = QueryOptions.builder(QueryMode.CONTEXTUAL)
                .context(ImmutableContextSet.of("world", world))
                .build();
        return Optional.of(user).map(PermissionHolder::getCachedData)
                .map(data -> data.getMetaData(options).getPrefix())
                .orElse("");
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {

    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        if (world == null || player == null) return "";
        var user = luckPerms.getUserManager().getUser(player);
        if (user == null) return "";
        var options = QueryOptions.builder(QueryMode.CONTEXTUAL)
                .context(ImmutableContextSet.of("world", world))
                .build();
        return Optional.of(user).map(PermissionHolder::getCachedData)
                .map(data -> data.getMetaData(options).getSuffix())
                .orElse("");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {

    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return "";
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {

    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return "";
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {

    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        return 0;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {

    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return 0;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {

    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        return 0;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {

    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return 0;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {

    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        return false;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {

    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return false;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {

    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        return "";
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {

    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return "";
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
