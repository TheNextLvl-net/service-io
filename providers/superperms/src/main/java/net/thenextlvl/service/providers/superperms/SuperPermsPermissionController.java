package net.thenextlvl.service.providers.superperms;

import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DoNotWrap
@NullMarked
public record SuperPermsPermissionController(Plugin plugin) implements PermissionController {
    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(getPermissionHolder(player).orElse(NoOpHolder.INSTANCE));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, final World world) {
        return loadPermissionHolder(player);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player) {
        final var onlinePlayer = player.getPlayer();
        return Optional.ofNullable(onlinePlayer).map(SuperPermsPermissionHolder::new);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, final World world) {
        return getPermissionHolder(player);
    }

    private Optional<PermissionHolder> getPermissionHolder(@Nullable final Player player) {
        return Optional.ofNullable(player).map(SuperPermsPermissionHolder::new);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return "SuperPerms";
    }
}
