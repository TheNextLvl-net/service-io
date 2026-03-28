package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The ChatController interface provides methods to retrieve a chat profile of a player.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
 */
public interface ChatController extends Controller {
    /**
     * Loads the chat profile for the given OfflinePlayer.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    default CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player) {
        return loadProfile(player, null);
    }

    /**
     * Loads the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player, @Nullable final World world) {
        return loadProfile(player.getUniqueId(), world);
    }

    /**
     * Loads the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> loadProfile(final UUID uuid) {
        return loadProfile(uuid, null);
    }

    /**
     * Loads the chat profile for the given UUID in the specified world.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> loadProfile(UUID uuid, @Nullable World world);

    /**
     * Resolves the chat profile for the given OfflinePlayer, loading from the backing store if not cached.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be resolved.
     * @return a CompletableFuture that will complete with the chat profile
     * @since 3.0.0
     */
    default CompletableFuture<ChatProfile> resolveProfile(final OfflinePlayer player) {
        return resolveProfile(player, null);
    }

    /**
     * Resolves the chat profile for the given OfflinePlayer in the specified world, loading if not cached.
     *
     * @param player The OfflinePlayer whose chat profile is to be resolved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     * @since 3.0.0
     */
    default CompletableFuture<ChatProfile> resolveProfile(final OfflinePlayer player, @Nullable final World world) {
        return resolveProfile(player.getUniqueId(), world);
    }

    /**
     * Resolves the chat profile for the given UUID, loading from the backing store if not cached.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be resolved.
     * @return A CompletableFuture that will complete with the chat profile.
     * @since 3.0.0
     */
    default CompletableFuture<ChatProfile> resolveProfile(final UUID uuid) {
        return resolveProfile(uuid, null);
    }

    /**
     * Resolves the chat profile for the given UUID in the specified world, loading if not cached.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be resolved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     * @since 3.0.0
     */
    default CompletableFuture<ChatProfile> resolveProfile(final UUID uuid, @Nullable final World world) {
        return getProfile(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadProfile(uuid, world));
    }

    /**
     * Retrieves the chat profile for the given OfflinePlayer.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(final OfflinePlayer player) {
        return getProfile(player, null);
    }

    /**
     * Retrieves the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(final OfflinePlayer player, @Nullable final World world) {
        return getProfile(player.getUniqueId(), world);
    }

    /**
     * Retrieves the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(final UUID uuid) {
        return getProfile(uuid, null);
    }

    /**
     * Retrieves the chat profile for the given UUID in the specified world.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return an optional containing the chat profile, or empty.
     */
    Optional<ChatProfile> getProfile(UUID uuid, @Nullable World world);
}
