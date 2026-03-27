package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.permission.PermissionController;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class PermissionConverter extends PlayerConverter<PermissionController> {
    @Override
    public CompletableFuture<Void> convert(final OfflinePlayer player, final PermissionController source, final PermissionController target) {
        return source.tryGetPermissionHolder(player).thenAccept(holder -> target.tryGetPermissionHolder(player)
                .thenAccept(targetHolder -> holder.getPermissions().forEach(targetHolder::setPermission)));
    }
}
