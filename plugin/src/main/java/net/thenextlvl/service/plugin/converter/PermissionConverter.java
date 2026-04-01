package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.permission.PermissionController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

final class PermissionConverter extends PlayerConverter<PermissionController> {
    public PermissionConverter(final Plugin plugin, final PermissionController source, final PermissionController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert(final OfflinePlayer player) {
        return source.resolvePermissionHolder(player).thenAccept(holder -> target.resolvePermissionHolder(player)
                .thenAccept(targetHolder -> holder.getPermissions().forEach(targetHolder::setPermission)));
    }
}
