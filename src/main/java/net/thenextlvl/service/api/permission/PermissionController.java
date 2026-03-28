package net.thenextlvl.service.api.permission;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The PermissionController interface represents a controller for managing permissions for players.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
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
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player, final World world) {
        return loadPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} asynchronously.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid);

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
     * Resolves the {@code PermissionHolder} for the specified {@code OfflinePlayer}, loading if not cached.
     *
     * @param player the player for whom to resolve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     * @since 3.0.0
     */
    default CompletableFuture<PermissionHolder> resolvePermissionHolder(final OfflinePlayer player) {
        return resolvePermissionHolder(player.getUniqueId());
    }

    /**
     * Resolves the {@code PermissionHolder} for the specified {@code OfflinePlayer} and {@code World},
     * loading from the backing store if not cached.
     *
     * @param player the player for whom to resolve the permission holder
     * @param world  the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     * @since 3.0.0
     */
    default CompletableFuture<PermissionHolder> resolvePermissionHolder(final OfflinePlayer player, final World world) {
        return getPermissionHolder(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(player, world));
    }

    /**
     * Resolves the {@code PermissionHolder} for the specified {@code UUID}, loading if not cached.
     *
     * @param uuid the unique ID of the player for whom to resolve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     * @since 3.0.0
     */
    default CompletableFuture<PermissionHolder> resolvePermissionHolder(final UUID uuid) {
        return getPermissionHolder(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(uuid));
    }

    /**
     * Resolves the {@code PermissionHolder} for the specified {@code UUID} and {@code World},
     * loading from the backing store if not cached.
     *
     * @param uuid  the unique ID of the player for whom to resolve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     * @since 3.0.0
     */
    default CompletableFuture<PermissionHolder> resolvePermissionHolder(final UUID uuid, final World world) {
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
    default Optional<PermissionHolder> getPermissionHolder(final OfflinePlayer player, final World world) {
        return getPermissionHolder(player.getUniqueId(), world);
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID}.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(final UUID uuid);

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World}.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(UUID uuid, World world);
}
