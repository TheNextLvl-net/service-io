package net.thenextlvl.service.api.permission;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The PermissionController interface represents a controller for managing permissions for players.
 *
 * @see PermissionHolder
 */
public interface PermissionController extends Controller {
    /**
     * Loads the {@code PermissionHolder} for the specified {@code OfflinePlayer} asynchronously.
     *
     * @param player the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player) {
        return loadPermissionHolder(player, null);
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
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        return loadPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} asynchronously.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid) {
        return loadPermissionHolder(uuid, null);
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} and {@code World} asynchronously.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    CompletableFuture<PermissionHolder> loadPermissionHolder(UUID uuid, @Nullable World world);

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer} or try to load it.
     *
     * @param player the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(final OfflinePlayer player) {
        return tryGetPermissionHolder(player, null);
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
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
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
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(final UUID uuid) {
        return tryGetPermissionHolder(uuid, null);
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World} or try to load it.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> tryGetPermissionHolder(final UUID uuid, @Nullable final World world) {
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
    default Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player) {
        return getPermissionHolder(player, null);
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
    default Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, @Nullable final World world) {
        return getPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID}.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default Optional<PermissionHolder> getPermissionHolder(final UUID uuid) {
        return getPermissionHolder(uuid, null);
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World}.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(UUID uuid, @Nullable World world);
}
