package net.thenextlvl.service.wrapper;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.api.permission.PermissionController;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VaultPermissionServiceWrapper extends Permission {
    private final @Nullable GroupController groupController;
    private final PermissionController permissionController;

    public VaultPermissionServiceWrapper(@Nullable GroupController groupController, PermissionController permissionController, ServicePlugin plugin) {
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
        return getGroupHolder(player, world)
                .map(holder -> holder.checkPermission(permission))
                .map(TriState::toBoolean)
                .orElse(false);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return getGroupHolder(player, world)
                .map(holder -> holder.addPermission(permission))
                .orElse(false);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return getGroupHolder(player, world)
                .map(holder -> holder.removePermission(permission))
                .orElse(false);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return groupController().getGroup(group).thenApply(group1 ->
                        group1.checkPermission(permission))
                .join().equals(TriState.TRUE);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        var groupController = groupController();
        return Optional.ofNullable(plugin.getServer().getWorld(world))
                .map(target -> groupController.createGroup(group, target))
                .orElseGet(() -> groupController.createGroup(group))
                .thenApply(created -> true)
                .join();
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        var groupController = groupController();
        return Optional.ofNullable(plugin.getServer().getWorld(world))
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
        return groupController().getGroups().join().stream()
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

    private Optional<GroupHolder> getGroupHolder(String player, @Nullable String world) {
        var groupController = groupController();
        return Optional.ofNullable(plugin.getServer().getPlayer(player))
                .map(online -> Optional.ofNullable(world)
                        .map(plugin.getServer()::getWorld)
                        .map(target -> groupController.getGroupHolder(online, target))
                        .orElseGet(() -> groupController.getGroupHolder(online))
                        .join());
    }
}