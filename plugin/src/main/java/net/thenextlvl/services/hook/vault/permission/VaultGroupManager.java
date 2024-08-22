package net.thenextlvl.services.hook.vault.permission;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.services.ServicePlugin;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class VaultGroupManager extends Permission {
    private final @NotNull String name = "GroupManager";
    private final @NotNull GroupManager groupManager;

    public VaultGroupManager(@NotNull ServicePlugin plugin) {
        this.groupManager = JavaPlugin.getPlugin(GroupManager.class);
        this.plugin = plugin;
    }

    private @NotNull Optional<OverloadedWorldHolder> getWorldHolder(@Nullable String player, @Nullable String world) {
        return Optional.ofNullable(world)
                .map(getGroupManager().getWorldsHolder()::getWorldData)
                .or(() -> Optional.ofNullable(player).map(getGroupManager().getWorldsHolder()::getWorldData));
    }

    private @NotNull Optional<User> getUser(@Nullable String player, @Nullable String world) {
        return getWorldHolder(player, world).flatMap(holder -> Optional.ofNullable(player).map(holder::getUser));
    }

    @Override
    public boolean playerHas(@Nullable String world, @NotNull String name, @NotNull String permission) {
        return getWorldHolder(name, world)
                .map(WorldDataHolder::getPermissionsHandler)
                .map(handler -> handler.permission(name, permission))
                .orElse(false);
    }

    @Override
    public boolean playerAdd(@Nullable String world, @NotNull String name, @NotNull String permission) {
        return getUser(name, world).map(user -> {
            user.addPermission(permission);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean playerRemove(@Nullable String world, @NotNull String name, @NotNull String permission) {
        return getUser(name, world)
                .map(user -> user.removePermission(permission))
                .orElse(false);
    }

    @Override
    public boolean groupHas(@Nullable String world, @NotNull String groupName, @NotNull String permission) {
        return getWorldHolder(null, world)
                .map(holder -> holder.getGroup(groupName))
                .map(group -> group.hasSamePermissionNode(permission))
                .orElse(false);
    }

    @Override
    public boolean groupAdd(@Nullable String world, @NotNull String groupName, @NotNull String permission) {
        return getWorldHolder(null, world)
                .map(holder -> holder.getGroup(groupName))
                .map(group -> {
                    group.addPermission(permission);
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean groupRemove(@Nullable String worldName, @NotNull String groupName, @NotNull String permission) {
        return getWorldHolder(null, worldName)
                .map(holder -> holder.getGroup(groupName))
                .map(group -> group.removePermission(permission))
                .orElse(false);
    }

    @Override
    public boolean playerInGroup(@Nullable String worldName, @Nullable String name, @NotNull String groupName) {
        return getWorldHolder(name, worldName)
                .map(WorldDataHolder::getPermissionsHandler)
                .map(handler -> handler.inGroup(name, groupName))
                .orElse(false);
    }

    @Override
    public boolean playerAddGroup(@Nullable String world, @Nullable String name, @NotNull String groupName) {
        return getWorldHolder(name, world).map(holder -> {
            var user = holder.getUser(name);
            var group = holder.getGroup(groupName);
            if (user == null || group == null) return false;
            if (user.getGroup().equals(holder.getDefaultGroup())) user.setGroup(group);
            else if (group.getInherits().contains(user.getGroup().getName().toLowerCase())) user.setGroup(group);
            else return user.addSubGroup(group);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean playerRemoveGroup(@Nullable String world, @Nullable String name, @NotNull String groupName) {
        return getWorldHolder(name, world).map(holder -> {
            var user = holder.getUser(name);
            var group = holder.getGroup(groupName);
            if (user == null || group == null) return false;
            if (user.getGroup().equals(group)) user.setGroup(holder.getDefaultGroup());
            else return user.removeSubGroup(group);
            return true;
        }).orElse(false);
    }

    @Override
    public @NotNull String[] getPlayerGroups(@Nullable String world, @Nullable String name) {
        return getWorldHolder(name, world)
                .map(WorldDataHolder::getPermissionsHandler)
                .map(handler -> handler.getGroups(name))
                .orElseGet(() -> new String[]{});
    }

    @Override
    public @Nullable String getPrimaryGroup(@Nullable String world, @Nullable String name) {
        return getWorldHolder(name, world)
                .map(WorldDataHolder::getPermissionsHandler)
                .map(handler -> handler.getGroup(name))
                .orElse(null);
    }

    @Override
    public @NotNull String[] getGroups() {
        return plugin.getServer().getWorlds().stream()
                .map(WorldInfo::getName)
                .map(getGroupManager().getWorldsHolder()::getWorldData)
                .map(OverloadedWorldHolder::getGroupList)
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