package net.thenextlvl.service.wrapper;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VaultChatServiceWrapper extends Chat {
    private final @Nullable GroupController groupController;
    private final ChatController chatController;
    private final ServicePlugin plugin;

    public VaultChatServiceWrapper(
            @NotNull Permission permission,
            @Nullable GroupController groupController,
            @NotNull ChatController chatController,
            @NotNull ServicePlugin plugin
    ) {
        super(permission);
        this.groupController = groupController;
        this.chatController = chatController;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return chatController.getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getPrefix).orElse("");
    }

    @Override
    public void setPlayerPrefix(String world, String player, @NotNull String prefix) {
        getProfile(world, player).map(profile -> profile.setPrefix(prefix));
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getSuffix).orElse("");
    }

    @Override
    public void setPlayerSuffix(String world, String player, @NotNull String suffix) {
        getProfile(world, player).map(profile -> profile.setSuffix(suffix));
    }

    @Override
    public String getGroupPrefix(String worldName, String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getPrefix).orElse("");
    }

    @Override
    public void setGroupPrefix(String world, String groupName, @NotNull String prefix) {
        getGroup(world, groupName).map(group -> group.setPrefix(prefix));
    }

    @Override
    public String getGroupSuffix(String worldName, String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getSuffix).orElse("");
    }

    @Override
    public void setGroupSuffix(String world, String groupName, @NotNull String suffix) {
        getGroup(world, groupName).map(group -> group.setSuffix(suffix));
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, @NotNull String node, int defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, @NotNull String node, int value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public int getGroupInfoInteger(String worldName, String groupName, @NotNull String node, int defaultValue) {
        return getGroup(worldName, groupName)
                .flatMap(group -> group.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, @NotNull String node, int value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, @NotNull String node, double defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, @NotNull String node, double value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, @NotNull String node, double defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, @NotNull String node, double value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, @NotNull String node, boolean defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, @NotNull String node, boolean value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, @NotNull String node, boolean defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, @NotNull String node, boolean value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public @Nullable String getPlayerInfoString(String world, String player, @NotNull String node, String defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoString(String world, String player, @NotNull String node, @NotNull String value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, value));
    }

    @Override
    public @Nullable String getGroupInfoString(String world, String groupName, @NotNull String node, String defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoString(String world, String groupName, @NotNull String node, @NotNull String value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, value));
    }

    private Optional<ChatProfile> getProfile(String worldName, String name) {
        return Optional.ofNullable(name).map(plugin.getServer()::getOfflinePlayerIfCached)
                .flatMap(player -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> chatController.tryGetProfile(player, world).join()));
    }

    private Optional<Group> getGroup(String worldName, String groupName) {
        return groupName != null ? Optional.ofNullable(groupController)
                .map(controller -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> controller.tryGetGroup(groupName, world).join())
                        .orElseGet(() -> controller.tryGetGroup(groupName).join()))
                : Optional.empty();
    }
}
