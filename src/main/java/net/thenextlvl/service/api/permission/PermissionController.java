package net.thenextlvl.service.api.permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The PermissionController interface represents a controller for managing permissions for players.
 *
 * @see PermissionHolder
 */
@NullMarked
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
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer} or try to load it.
     *
     * @param player the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(OfflinePlayer player) {
        return getPermissionHolder(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(player));
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer} or try to load it.
     * and {@code World}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @param world  the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(OfflinePlayer player, World world) {
        return getPermissionHolder(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(player, world));
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} or try to load it.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(UUID uuid) {
        return getPermissionHolder(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(uuid));
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World} or try to load it.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(UUID uuid, World world) {
        return getPermissionHolder(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(uuid, world));
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player) {
        return getPermissionHolder(player.getUniqueId());
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer}
     * and {@code World}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @param world  the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world) {
        return getPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID}.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(UUID uuid);

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World}.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world);

    /**
     * Retrieves the name associated with the permission controller.
     *
     * @return the name of the chat controller.
     */
    @Contract(pure = true)
    String getName();
}
