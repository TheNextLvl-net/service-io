package net.thenextlvl.service.providers.groupmanager;

import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.DoNotWrap;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

@DoNotWrap
public final class GroupManagerPermission extends Permission {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    public GroupManagerPermission() {
        super.plugin = groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager";
    }

    @Override
    public boolean isEnabled() {
        return groupManager.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(@Nullable final String world, final String player, final String permission) {
        return getWorldHolder(player, world)
                .map(handler -> handler.permission(player, permission))
                .orElse(false);
    }

    @Override
    public boolean playerAdd(@Nullable final String world, final String player, final String permission) {
        return getWorldHolder(world).flatMap(holder -> getUser(holder, player).map(user -> {
            user.addPermission(permission);
            return true;
        })).orElse(false);
    }

    @Override
    public boolean playerRemove(@Nullable final String world, final String player, final String permission) {
        return getWorldHolder(world).flatMap(holder -> getUser(holder, player).map(user -> {
            return user.removePermission(permission);
        })).orElse(false);
    }

    @Override
    public boolean groupHas(@Nullable final String world, final String group, final String permission) {
        return getWorldHolder(world).flatMap(holder -> getGroup(holder, group).map(g -> {
            return g.hasSamePermissionNode(permission);
        })).orElse(false);
    }

    @Override
    public boolean groupAdd(@Nullable final String world, final String group, final String permission) {
        return getWorldHolder(world).flatMap(holder -> getGroup(holder, group).map(g -> {
            g.addPermission(permission);
            return true;
        })).orElse(false);
    }

    @Override
    public boolean groupRemove(@Nullable final String world, final String group, final String permission) {
        return getWorldHolder(world).flatMap(holder -> getGroup(holder, group).map(g -> {
            g.removePermission(permission);
            return true;
        })).orElse(false);
    }

    @Override
    public boolean playerInGroup(@Nullable final String world, final String player, final String group) {
        return getWorldHolder(player, world).map(handler -> handler.inGroup(player, group)).orElse(false);
    }

    @Override
    public boolean playerAddGroup(@Nullable final String world, final String player, final String group) {
        return getWorldHolder(world).flatMap(holder -> getUser(holder, player)
                .flatMap(user -> getGroup(holder, group).map(user::addSubGroup))
        ).orElse(false);
    }

    @Override
    public boolean playerRemoveGroup(@Nullable final String world, final String player, final String group) {
        return getWorldHolder(world).flatMap(holder -> getUser(holder, player)
                .flatMap(user -> getGroup(holder, group).map(user::removeSubGroup))
        ).orElse(false);
    }

    @Override
    public String[] getPlayerGroups(@Nullable final String world, final String player) {
        return getWorldHolder(player, world)
                .map(handler -> handler.getGroups(player))
                .orElse(new String[0]);
    }

    @Override
    public String getPrimaryGroup(@Nullable final String world, final String player) {
        return getWorldHolder(player, world)
                .map(handler -> handler.getGroup(player))
                .orElse(null);
    }

    @Override
    public String[] getGroups() {
        final var groups = new HashSet<String>();
        for (final var world : groupManager.getServer().getWorlds()) {
            final var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
            if (holder != null) holder.getGroupList().forEach(group -> groups.add(group.getName()));
        }
        return groups.toArray(new String[0]);
    }

    private Optional<Group> getGroup(final OverloadedWorldHolder holder, final String name) {
        return Optional.ofNullable(holder.getGroup(name));
    }

    private Optional<User> getUser(final OverloadedWorldHolder holder, final String name) {
        return Optional.ofNullable(holder.getUser(name));
    }

    private Optional<OverloadedWorldHolder> getWorldHolder(@Nullable final String world) {
        return Optional.ofNullable(world)
                .map(groupManager.getWorldsHolder()::getWorldData)
                .or(() -> Optional.ofNullable(groupManager.getWorldsHolder().getDefaultWorld()));
    }

    private Optional<AnjoPermissionsHandler> getWorldHolder(final String player, @Nullable final String world) {
        if (world != null)
            return Optional.ofNullable(groupManager.getWorldsHolder().getWorldPermissions(world));
        return Optional.ofNullable(groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(player));
    }

    private Optional<OverloadedWorldHolder> getWorldDataHolder(final String player, @Nullable final String world) {
        if (world != null)
            return Optional.ofNullable(groupManager.getWorldsHolder().getWorldData(world));
        return Optional.ofNullable(groupManager.getWorldsHolder().getWorldDataByPlayerName(player));
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
