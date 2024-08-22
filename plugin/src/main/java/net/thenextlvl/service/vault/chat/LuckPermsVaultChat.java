package net.thenextlvl.service.vault.chat;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class LuckPermsVaultChat extends Chat {
    private final @NotNull String name = "LuckPerms - Chat";
    private final @NotNull LuckPerms luckPerms;

    public LuckPermsVaultChat(@NotNull ServicePlugin plugin, @NotNull Permission permission) {
        super(permission);
        this.luckPerms = LuckPermsProvider.get();
    }

    private @NotNull Optional<User> getUser(@Nullable String name) {
        return Optional.ofNullable(name).map(getLuckPerms().getUserManager()::getUser);
    }

    private @NotNull Optional<Group> getGroup(@Nullable String name) {
        return Optional.ofNullable(name).map(getLuckPerms().getGroupManager()::getGroup);
    }

    private @NotNull Optional<CachedMetaData> getMetaData(@NotNull CachedDataManager manager, @Nullable String world) {
        var options = Optional.ofNullable(world).map(s -> QueryOptions.contextual(ImmutableContextSet.of("world", s)));
        return options.map(manager::getMetaData).or(() -> Optional.of(manager.getMetaData()));
    }

    @Override
    public @NotNull String getPlayerPrefix(@Nullable String world, @Nullable String player) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(CachedMetaData::getPrefix)
                .orElse("");
    }

    @Override
    public void setPlayerPrefix(@Nullable String world, @Nullable String player, @NotNull String prefix) {
        getUser(player).ifPresent(user -> {
            var data = user.data();
            data.add(PrefixNode.builder().prefix(prefix).build());
            getLuckPerms().getUserManager().saveUser(user);
        });
    }

    @Override
    public @NotNull String getPlayerSuffix(@Nullable String world, @Nullable String player) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(CachedMetaData::getSuffix)
                .orElse("");
    }

    @Override
    public void setPlayerSuffix(@Nullable String world, @Nullable String player, @NotNull String suffix) {
        getUser(player).ifPresent(user -> {
            var data = user.data();
            data.add(SuffixNode.builder().suffix(suffix).build());
            getLuckPerms().getUserManager().saveUser(user);
        });
    }

    @Override
    public @NotNull String getGroupPrefix(@Nullable String world, @Nullable String group) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(CachedMetaData::getPrefix)
                .orElse("");
    }

    @Override
    public void setGroupPrefix(@Nullable String world, @Nullable String name, @NotNull String prefix) {
        getGroup(name).ifPresent(group -> {
            var data = group.data();
            data.add(PrefixNode.builder().prefix(prefix).build());
            getLuckPerms().getGroupManager().saveGroup(group);
        });
    }

    @Override
    public @NotNull String getGroupSuffix(@Nullable String world, @Nullable String group) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(CachedMetaData::getSuffix)
                .orElse("");
    }

    @Override
    public void setGroupSuffix(@Nullable String world, @Nullable String name, @NotNull String suffix) {
        getGroup(name).ifPresent(group -> {
            var data = group.data();
            data.add(SuffixNode.builder().suffix(suffix).build());
            getLuckPerms().getGroupManager().saveGroup(group);
        });
    }

    @Override
    public int getPlayerInfoInteger(@Nullable String world, @Nullable String player, @NotNull String node, int defaultValue) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Integer::parseInt))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(@Nullable String world, @Nullable String player, @NotNull String node, int value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public int getGroupInfoInteger(@Nullable String world, @Nullable String group, @NotNull String node, int defaultValue) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Integer::parseInt))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoInteger(@Nullable String world, @Nullable String name, @NotNull String node, int value) {
        setGroupInfoString(world, name, node, String.valueOf(value));
    }

    @Override
    public double getPlayerInfoDouble(@Nullable String world, @Nullable String player, @NotNull String node, double defaultValue) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Double::parseDouble))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(@Nullable String world, @Nullable String player, @NotNull String node, double value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(@Nullable String world, @Nullable String group, @NotNull String node, double defaultValue) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Double::parseDouble))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoDouble(@Nullable String world, @Nullable String group, @NotNull String node, double value) {
        setGroupInfoString(world, group, node, String.valueOf(value));
    }

    @Override
    public boolean getPlayerInfoBoolean(@Nullable String world, @Nullable String player, @NotNull String node, boolean defaultValue) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Boolean::parseBoolean))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(@Nullable String world, @Nullable String player, @NotNull String node, boolean value) {
        setPlayerInfoString(world, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupInfoBoolean(@Nullable String world, @Nullable String group, @NotNull String node, boolean defaultValue) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .flatMap(data -> data.getMetaValue(node, Boolean::parseBoolean))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(@Nullable String world, @Nullable String group, @NotNull String node, boolean value) {
        setGroupInfoString(world, group, node, String.valueOf(value));
    }

    @Override
    public @Nullable String getPlayerInfoString(@Nullable String world, @Nullable String player, @NotNull String node, @Nullable String defaultValue) {
        return getUser(player).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(data -> data.getMetaValue(node))
                .orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoString(@Nullable String world, @Nullable String player, @NotNull String node, @NotNull String value) {
        getUser(player).ifPresent(user -> {
            var data = user.data();
            data.add(MetaNode.builder(node, value).build());
            getLuckPerms().getUserManager().saveUser(user);
        });
    }

    @Override
    public @Nullable String getGroupInfoString(@Nullable String world, @Nullable String group, @NotNull String node, @Nullable String defaultValue) {
        return getGroup(group).map(PermissionHolder::getCachedData)
                .flatMap(manager -> getMetaData(manager, world))
                .map(data -> data.getMetaValue(node))
                .orElse(defaultValue);
    }

    @Override
    public void setGroupInfoString(@Nullable String world, @Nullable String name, @NotNull String node, @NotNull String value) {
        getGroup(name).ifPresent(group -> {
            var data = group.data();
            data.add(MetaNode.builder(node, value).build());
            getLuckPerms().getGroupManager().saveGroup(group);
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
