package net.thenextlvl.service.controller.permission;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import net.thenextlvl.service.model.permission.SuperPermsPermissionHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class SuperPermsPermissionController implements PermissionController {
    private final ServicePlugin plugin;

    public SuperPermsPermissionController(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, @Nullable World world) {
        return CompletableFuture.completedFuture(getPermissionHolder(player.getPlayer()).orElse(null));
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, @Nullable World world) {
        return CompletableFuture.completedFuture(getPermissionHolder(uuid, world).orElse(null));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(UUID uuid, @Nullable World world) {
        return getPermissionHolder(plugin.getServer().getPlayer(uuid));
    }

    private Optional<PermissionHolder> getPermissionHolder(@Nullable Player player) {
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
