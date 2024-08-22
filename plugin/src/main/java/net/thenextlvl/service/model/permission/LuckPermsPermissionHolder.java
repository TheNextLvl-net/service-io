package net.thenextlvl.service.model.permission;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.permission.PermissionHolder;

public record LuckPermsPermissionHolder(User user, QueryOptions options) implements PermissionHolder {
    @Override
    public TriState checkPermission(String permission) {
        return switch (user().getCachedData().getPermissionData(options()).checkPermission(permission)) {
            case FALSE -> TriState.FALSE;
            case TRUE -> TriState.TRUE;
            case UNDEFINED -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        var result = user().data().add(PermissionNode.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removePermission(String permission) {
        var result = user().data().add(PermissionNode.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }
}
