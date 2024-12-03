package net.thenextlvl.service.wrapper;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class VaultPermissionServiceWrapper extends Permission {
    private final @Nullable GroupController groupController;
    private final PermissionController permissionController;

    public VaultPermissionServiceWrapper(
            @Nullable GroupController groupController,
            PermissionController permissionController,
            Plugin plugin
    ) {
        this.groupController = groupController;
        this.permissionController = permissionController;
        super.plugin = plugin;
    }

    @Override
    public String getName() {
        return permissionController.getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.checkPermission(permission))
                .map(TriState::toBoolean)
                .orElse(false);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.addPermission(permission))
                .orElse(false);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.removePermission(permission))
                .orElse(false);
    }

    @Override
        return groupController().getGroup(groupName)
    public boolean groupHas(String world, String groupName, String permission) {
                .map(group -> group.checkPermission(permission))
                .map(TriState.TRUE::equals)
                .orElse(false);
    }

    @Override
    public boolean groupAdd(String world, String groupName, String permission) {
        var groupController = groupController();
        return Optional.ofNullable(world)
                .map(plugin.getServer()::getWorld)
                .map(target -> groupController.createGroup(group, target))
                .orElseGet(() -> groupController.createGroup(group))
                .thenApply(created -> true)
                .join();
    }

    @Override
    public boolean groupRemove(String world, String groupName, String permission) {
        var groupController = groupController();
        return Optional.ofNullable(world)
                .map(plugin.getServer()::getWorld)
                .map(target -> groupController.deleteGroup(group, target))
                .orElseGet(() -> groupController.deleteGroup(group))
                .join();
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.inGroup(group))
                .orElse(false);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.addGroup(group))
                .orElse(false);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.removeGroup(group))
                .orElse(false);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return getGroupHolder(player, world).map(holder ->
                        holder.getGroups().stream()
                                .map(Group::getName)
                                .toArray(String[]::new))
                .orElseGet(() -> new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return getGroupHolder(player, world)
                .map(GroupHolder::getPrimaryGroup)
                .orElse("");
    }

    @Override
    public String[] getGroups() {
        return groupController().getGroups().stream()
                .map(Group::getName)
                .toArray(String[]::new);
    }

    @Override
    public boolean hasGroupSupport() {
        return groupController != null;
    }

    private GroupController groupController() throws UnsupportedOperationException {
        if (groupController != null) return groupController;
        throw new UnsupportedOperationException(getName() + " has no group support");
    }

    private Optional<PermissionHolder> getPermissionHolder(String player, String world) {
        return Optional.ofNullable(player)
                .map(plugin.getServer()::getOfflinePlayerIfCached)
                .map(offline -> Optional.ofNullable(world)
                        .map(plugin.getServer()::getWorld)
                        .map(target -> permissionController.loadPermissionHolder(offline, target).join())
                        .orElseGet(() -> permissionController.loadPermissionHolder(offline).join()));
    }

    private Optional<GroupHolder> getGroupHolder(String player, String world) {
        var groupController = groupController();
        return Optional.ofNullable(player)
                .map(plugin.getServer()::getOfflinePlayerIfCached)
                .map(offline -> Optional.ofNullable(world)
                        .map(plugin.getServer()::getWorld)
                        .map(target -> groupController.tryGetGroupHolder(offline, target).join())
                        .orElseGet(() -> groupController.tryGetGroupHolder(offline).join()));
    }
}
