package net.thenextlvl.service.vault.permission;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@Getter
public class VaultLuckPerms extends Permission {
    private final String name = "LuckPerms";
    private final LuckPerms luckPerms;

    public VaultLuckPerms(@NotNull ServicePlugin plugin) {
        var servicesManager = plugin.getServer().getServicesManager();
        this.luckPerms = Objects.requireNonNull(servicesManager.load(LuckPerms.class), "LuckPerms not found");
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
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
    public boolean playerHas(String world, String player, String permission) {
        if (player == null || permission == null) return false;
        var user = luckPerms.getUserManager().getUser(player);
        if (user == null) return false;
        return world != null ? user.getCachedData().getPermissionData(QueryOptions.builder(QueryMode.CONTEXTUAL)
                .context(ImmutableContextSet.of("world", world)).build()
        ).checkPermission(permission).asBoolean() : user.getCachedData().getPermissionData()
                .checkPermission(permission).asBoolean();
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return "";
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
