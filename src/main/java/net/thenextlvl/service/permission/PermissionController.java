package net.thenextlvl.service.permission;

import net.thenextlvl.service.Controller;
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
    CompletableFuture<PermissionHolder> loadPermissionHolder(final OfflinePlayer player);

    /**
     * Loads the {@code PermissionHolder} for the specified {@code OfflinePlayer} asynchronously.
     * and {@code World}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @param world  the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    CompletableFuture<PermissionHolder> loadPermissionHolder(OfflinePlayer player, World world);

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} asynchronously.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid) {
        return loadPermissionHolder(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Loads the {@code PermissionHolder} for the specified {@code UUID} and {@code World} asynchronously.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     */
    default CompletableFuture<PermissionHolder> loadPermissionHolder(final UUID uuid, final World world) {
        return loadPermissionHolder(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Resolves the {@code PermissionHolder} for the specified {@code OfflinePlayer}, loading if not cached.
     *
     * @param player the player for whom to resolve the permission holder
     * @return a {@code CompletableFuture} that will complete with the permission holder
     * @see PermissionHolder
     * @since 3.0.0
     */
    default CompletableFuture<PermissionHolder> resolvePermissionHolder(final OfflinePlayer player) {
        return getPermissionHolder(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadPermissionHolder(player));
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
     * @return an optional containing the permission holder, or empty
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player);

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code OfflinePlayer}
     * and {@code World}.
     *
     * @param player the player for whom to retrieve the permission holder
     * @param world  the world in which the permission holder exists
     * @return an optional containing the permission holder, or empty
     * @see PermissionHolder
     */
    Optional<PermissionHolder> getPermissionHolder(OfflinePlayer player, World world);

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID}.
     *
     * @param uuid the unique ID of the player for whom to retrieve the permission holder
     * @return an optional containing the permission holder, or empty
     * @see PermissionHolder
     */
    default Optional<PermissionHolder> getPermissionHolder(final UUID uuid) {
        return getPermissionHolder(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Retrieves the {@code PermissionHolder} for the specified {@code UUID} and {@code World}.
     *
     * @param uuid  the unique ID of the player for whom to retrieve the permission holder
     * @param world the world in which the permission holder exists
     * @return an optional containing the permission holder, or empty
     * @see PermissionHolder
     */
    default Optional<PermissionHolder> getPermissionHolder(final UUID uuid, final World world) {
        return getPermissionHolder(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }
}
