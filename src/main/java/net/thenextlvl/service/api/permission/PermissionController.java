package net.thenextlvl.service.api.permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The PermissionController interface represents a controller for managing permissions for players.
 *
 * @see PermissionHolder
 */
public interface PermissionController {
    /**
     * Loads the {@code PermissionHolder} for the specified {@code OfflinePlayer} asynchronously.
     *
     * @param player the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player) {
        return loadPermissionHolder(player.getUniqueId());
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code OfflinePlayer} asynchronously.
     * and {@code World}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @param world  the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, World world) {
        return loadPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} asynchronously.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid);

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} and {@code World} asynchronously.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, World world);

    /**
     * Retrieves the name associated with the permission controller.
     *
     * @return the name of the chat controller.
     */
    @Contract(pure = true)
    String getName();
}
