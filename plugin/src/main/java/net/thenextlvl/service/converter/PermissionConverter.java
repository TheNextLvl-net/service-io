package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.permission.PermissionController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
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
