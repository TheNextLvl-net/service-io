package net.thenextlvl.service.providers.groupmanager;

import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DoNotWrap
@NullMarked
public final class GroupManagerPermissionController implements PermissionController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player) {
        return loadPermissionHolder(player, null);
    }

    @Override
    public CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        return CompletableFuture.completedFuture(getPermissionHolder(player, world).orElse(null));
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player) {
        return getPermissionHolder(player, null);
    }

    @Override
    public Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        final var holder = world != null
                ? groupManager.getWorldsHolder().getWorldData(world.getName())
                : groupManager.getWorldsHolder().getDefaultWorld();
        return getHolder(holder, player.getUniqueId());
    }

    private Optional<PermissionHolder> getHolder(@Nullable final WorldDataHolder holder, final UUID uuid) {
        if (holder == null) return Optional.empty();
        return Optional.ofNullable(holder.getUser(uuid.toString()))
                .map(user -> new GroupManagerPermissionHolder(user, holder));
    }

    @Override
    public Plugin getPlugin() {
        return groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager";
    }
}
