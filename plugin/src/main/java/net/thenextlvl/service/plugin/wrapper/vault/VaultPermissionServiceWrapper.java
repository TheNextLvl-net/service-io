package net.thenextlvl.service.plugin.wrapper.vault;

import net.kyori.adventure.util.TriState;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.group.GroupHolder;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class VaultPermissionServiceWrapper extends Permission implements Wrapper {
    private final @Nullable GroupController groupController;
    private final PermissionController permissionController;

    public VaultPermissionServiceWrapper(
            @Nullable final GroupController groupController,
            final PermissionController permissionController,
            final Plugin plugin
    ) {
        this.groupController = groupController;
        this.permissionController = permissionController;
        super.plugin = plugin;
    }

    @Override
    public String getName() {
        return permissionController.getName() + " Wrapper";
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
    public boolean playerHas(@Nullable final String world, final String player, final String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.checkPermission(permission))
                .map(TriState::toBoolean)
                .orElse(false);
    }

    @Override
    public boolean playerAdd(@Nullable final String world, final String player, final String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.addPermission(permission))
                .orElse(false);
    }

    @Override
    public boolean playerRemove(@Nullable final String world, final String player, final String permission) {
        return getPermissionHolder(player, world)
                .map(holder -> holder.removePermission(permission))
                .orElse(false);
    }

    @Override
    public boolean groupHas(@Nullable final String world, final String groupName, final String permission) {
        final var groupController = groupController();
        return Optional.ofNullable(world)
                .map(getPlugin().getServer()::getWorld)
                .flatMap(target -> groupController.getGroup(groupName, target)
                        .or(() -> groupController.getGroup(groupName)))
                .map(group -> group.checkPermission(permission))
                .map(TriState.TRUE::equals)
                .orElse(false);
    }

    @Override
    public boolean groupAdd(@Nullable final String world, final String groupName, final String permission) {
        final var groupController = groupController();
        return Optional.ofNullable(world)
                .map(getPlugin().getServer()::getWorld)
                .flatMap(target -> groupController.getGroup(groupName, target)
                        .or(() -> groupController.getGroup(groupName)))
                .map(group -> group.addPermission(permission))
                .orElse(false);
    }

    @Override
    public boolean groupRemove(@Nullable final String world, final String groupName, final String permission) {
        final var groupController = groupController();
        return Optional.ofNullable(world)
                .map(getPlugin().getServer()::getWorld)
                .flatMap(target -> groupController.getGroup(groupName, target)
                        .or(() -> groupController.getGroup(groupName)))
                .map(group -> group.removePermission(permission))
                .orElse(false);
    }

    @Override
    public boolean playerInGroup(@Nullable final String world, final String player, final String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.inGroup(group))
                .orElse(false);
    }

    @Override
    public boolean playerAddGroup(@Nullable final String world, final String player, final String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.addGroup(group))
                .orElse(false);
    }

    @Override
    public boolean playerRemoveGroup(@Nullable final String world, final String player, final String group) {
        return getGroupHolder(player, world)
                .map(holder -> holder.removeGroup(group))
                .orElse(false);
    }

    @Override
    public String[] getPlayerGroups(@Nullable final String world, final String player) {
        return getGroupHolder(player, world).map(holder ->
                        holder.getGroups().stream()
                                .map(Group::getName)
                                .toArray(String[]::new))
                .orElseGet(() -> new String[0]);
    }

    @Override
    public String getPrimaryGroup(@Nullable final String world, final String player) {
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

    private Optional<PermissionHolder> getPermissionHolder(@Nullable final String player, @Nullable final String world) {
        return Optional.ofNullable(player)
                .map(getPlugin().getServer()::getOfflinePlayerIfCached)
                .map(offline -> Optional.ofNullable(world)
                        .map(getPlugin().getServer()::getWorld)
                        .map(target -> permissionController.loadPermissionHolder(offline, target).join())
                        .orElseGet(() -> permissionController.loadPermissionHolder(offline).join()));
    }

    private Optional<GroupHolder> getGroupHolder(@Nullable final String player, @Nullable final String world) {
        final var groupController = groupController();
        return Optional.ofNullable(player)
                .map(getPlugin().getServer()::getOfflinePlayerIfCached)
                .map(offline -> Optional.ofNullable(world)
                        .map(getPlugin().getServer()::getWorld)
                        .map(target -> groupController.resolveGroupHolder(offline, target).join())
                        .orElseGet(() -> groupController.resolveGroupHolder(offline).join()));
    }

    private Plugin getPlugin() {
        assert plugin != null;
        return plugin;
    }
}
