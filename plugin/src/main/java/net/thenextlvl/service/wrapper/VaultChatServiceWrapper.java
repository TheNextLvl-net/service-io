package net.thenextlvl.service.wrapper;

import net.milkbowl.vault.chat.Chat;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VaultChatServiceWrapper extends Chat {
    private final VaultPermissionServiceWrapper wrapper;
    private final ChatController controller;
    private final ServicePlugin plugin;

    public VaultChatServiceWrapper(VaultPermissionServiceWrapper wrapper, ChatController controller, ServicePlugin plugin) {
        super(wrapper);
        this.wrapper = wrapper;
        this.controller = controller;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private Optional<ChatProfile> getProfile(@Nullable String worldName, @Nullable String name) {
        return Optional.ofNullable(name).map(plugin.getServer()::getPlayer)
                .flatMap(player -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> controller.getProfile(player, world).join()));
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getPrefix).orElse("");
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        getProfile(world, player).map(profile -> profile.setPrefix(prefix));
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return getProfile(world, player).flatMap(ChatProfile::getSuffix).orElse("");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        getProfile(world, player).map(profile -> profile.setSuffix(suffix));
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
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.intInfoNode(node))
                .orElse(defaultValue);
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
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.doubleInfoNode(node))
                .orElse(defaultValue);
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
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.booleanInfoNode(node))
                .orElse(defaultValue);
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
        return getProfile(world, player)
                .flatMap(chatProfile -> chatProfile.getInfoNode(node))
                .orElse(defaultValue);
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
}
