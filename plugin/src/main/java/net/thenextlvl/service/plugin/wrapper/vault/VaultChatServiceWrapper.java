package net.thenextlvl.service.plugin.wrapper.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.chat.ChatProfile;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class VaultChatServiceWrapper extends Chat implements Wrapper {
    private final @Nullable GroupController groupController;
    private final ChatController chatController;
    private final Plugin plugin;

    public VaultChatServiceWrapper(
            final Permission permission,
            @Nullable final GroupController groupController,
            final ChatController chatController,
            final Plugin plugin
    ) {
        super(permission);
        this.groupController = groupController;
        this.chatController = chatController;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        if (chatController instanceof Wrapper) return chatController.getName();
        return chatController.getName() + " Wrapper";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPlayerPrefix(@Nullable final String world, final String player) {
        return getProfile(world, player).flatMap(ChatProfile::getPrefix).orElse("");
    }

    @Override
    public void setPlayerPrefix(@Nullable final String world, final String player, final String prefix) {
        getProfile(world, player).ifPresent(profile -> profile.setPrefix(prefix));
    }

    @Override
    public String getPlayerSuffix(@Nullable final String world, final String player) {
        return getProfile(world, player).flatMap(ChatProfile::getSuffix).orElse("");
    }

    @Override
    public void setPlayerSuffix(@Nullable final String world, final String player, final String suffix) {
        getProfile(world, player).map(profile -> profile.setSuffix(suffix));
    }

    @Override
    public String getGroupPrefix(@Nullable final String worldName, final String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getPrefix).orElse("");
    }

    @Override
    public void setGroupPrefix(@Nullable final String world, final String groupName, final String prefix) {
        getGroup(world, groupName).ifPresent(group -> group.setPrefix(prefix));
    }

    @Override
    public String getGroupSuffix(@Nullable final String worldName, final String groupName) {
        return getGroup(worldName, groupName).flatMap(Group::getSuffix).orElse("");
    }

    @Override
    public void setGroupSuffix(@Nullable final String world, final String groupName, final String suffix) {
        getGroup(world, groupName).ifPresent(group -> group.setSuffix(suffix));
    }

    @Override
    public int getPlayerInfoInteger(@Nullable final String world, final String player, final String node, final int defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(@Nullable final String world, final String player, final String node, final int value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public int getGroupInfoInteger(@Nullable final String world, final String groupName, final String node, final int defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.intInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoInteger(@Nullable final String world, final String groupName, final String node, final int value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, String.valueOf(value)));
    }

    @Override
    public double getPlayerInfoDouble(@Nullable final String world, final String player, final String node, final double defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(@Nullable final String world, final String player, final String node, final double value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(@Nullable final String world, final String groupName, final String node, final double defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.doubleInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoDouble(@Nullable final String world, final String groupName, final String node, final double value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public boolean getPlayerInfoBoolean(@Nullable final String world, final String player, final String node, final boolean defaultValue) {
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(@Nullable final String world, final String player, final String node, final boolean value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupInfoBoolean(@Nullable final String world, final String groupName, final String node, final boolean defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.booleanInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(@Nullable final String world, final String groupName, final String node, final boolean value) {
        setGroupInfoString(world, groupName, node, String.valueOf(value));
    }

    @Override
    public String getPlayerInfoString(@Nullable final String world, final String player, final String node, final String defaultValue) {
        return getProfile(world, player)
                .flatMap(profile -> profile.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoString(@Nullable final String world, final String player, final String node, final String value) {
        getProfile(world, player).ifPresent(profile -> profile.setInfoNode(node, value));
    }

    @Override
    public String getGroupInfoString(@Nullable final String world, final String groupName, final String node, final String defaultValue) {
        return getGroup(world, groupName)
                .flatMap(group -> group.getInfoNode(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoString(@Nullable final String world, final String groupName, final String node, final String value) {
        getGroup(world, groupName).ifPresent(group -> group.setInfoNode(node, value));
    }

    private Optional<ChatProfile> getProfile(@Nullable final String worldName, final String name) {
        return Optional.ofNullable(plugin.getServer().getOfflinePlayerIfCached(name))
                .flatMap(player -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> chatController.resolveProfile(player, world).join()));
    }

    private Optional<Group> getGroup(@Nullable final String worldName, final String groupName) {
        return Optional.ofNullable(groupController).map(controller -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .map(world -> controller.resolveGroup(groupName, world).join())
                .orElseGet(() -> controller.resolveGroup(groupName).join()));
    }
}
