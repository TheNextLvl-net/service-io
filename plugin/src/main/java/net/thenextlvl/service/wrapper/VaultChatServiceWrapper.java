package net.thenextlvl.service.wrapper;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public class VaultChatServiceWrapper extends Chat {
    private final @Nullable GroupController groupController;
    private final ChatController chatController;
    private final ServicePlugin plugin;

    public VaultChatServiceWrapper(
            Permission permission,
            @Nullable GroupController groupController,
            ChatController chatController,
            ServicePlugin plugin
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
    public String getPlayerPrefix(@Nullable String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getPrefix).orElse("");
    }

    @Override
    public void setPlayerPrefix(@Nullable String world, String player, String prefix) {
        getProfile(world, player).ifPresent(profile -> profile.setPrefix(prefix));
    }

    @Override
    public String getPlayerSuffix(@Nullable String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getSuffix).orElse("");
    }

    @Override
    public void setPlayerSuffix(@Nullable String world, String player, String suffix) {
        getProfile(world, player).map(profile -> profile.setSuffix(suffix));
    }

    @Override
    public String getGroupPrefix(@Nullable String worldName, String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getPrefix).orElse("");
    }

    @Override
    public void setGroupPrefix(@Nullable String world, String groupName, String prefix) {
        getGroup(world, groupName).ifPresent(group -> group.setPrefix(prefix));
    }

    @Override
    public String getGroupSuffix(@Nullable String worldName, String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getSuffix).orElse("");
    }

    @Override
    public void setGroupSuffix(@Nullable String world, String groupName, String suffix) {
        getGroup(world, groupName).ifPresent(group -> group.setSuffix(suffix));
    }

    @Override
    public int getPlayerInfoInteger(@Nullable String world, String player, String node, int defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(@Nullable String world, String player, String node, int value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public int getGroupInfoInteger(@Nullable String world, String groupName, String node, int defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoInteger(@Nullable String world, String groupName, String node, int value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public double getPlayerInfoDouble(@Nullable String world, String player, String node, double defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(@Nullable String world, String player, String node, double value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(@Nullable String world, String groupName, String node, double defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoDouble(@Nullable String world, String groupName, String node, double value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public boolean getPlayerInfoBoolean(@Nullable String world, String player, String node, boolean defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(@Nullable String world, String player, String node, boolean value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupInfoBoolean(@Nullable String world, String groupName, String node, boolean defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(@Nullable String world, String groupName, String node, boolean value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public String getPlayerInfoString(@Nullable String world, String player, String node, String defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoString(@Nullable String world, String player, String node, String value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, value));
    }

    @Override
    public String getGroupInfoString(@Nullable String world, String groupName, String node, String defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoString(@Nullable String world, String groupName, String node, String value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, value));
    }

    private Optional<ChatProfile> getProfile(@Nullable String worldName, String name) {
        return Optional.ofNullable(plugin.getServer().getOfflinePlayerIfCached(name))
                .flatMap(player -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> chatController.tryGetProfile(player, world).join()));
    }

    private Optional<Group> getGroup(@Nullable String worldName, String groupName) {
        return Optional.ofNullable(groupController).map(controller -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .map(world -> controller.tryGetGroup(groupName, world).join())
                .orElseGet(() -> controller.tryGetGroup(groupName).join()));
    }
}
