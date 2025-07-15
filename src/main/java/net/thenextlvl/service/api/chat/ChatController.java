package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The ChatController interface provides methods to retrieve a chat profile of a player.
 *
 * @since 1.0.0
 */
@NullMarked
public interface ChatController extends Controller {
    /**
     * Loads the chat profile for the given OfflinePlayer.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    default CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player) {
        return loadProfile(player, null);
    }

    /**
     * Loads the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, @Nullable World world) {
        return loadProfile(player.getUniqueId(), world);
    }

    /**
     * Loads the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> loadProfile(UUID uuid) {
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
     * Retrieves the chat profile for the given OfflinePlayer or try to load it.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    default CompletableFuture<ChatProfile> tryGetProfile(OfflinePlayer player) {
        return tryGetProfile(player, null);
    }

    /**
     * Retrieve the chat profile for the given OfflinePlayer in the specified world or try to load it.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(OfflinePlayer player, @Nullable World world) {
        return tryGetProfile(player.getUniqueId(), world);
    }

    /**
     * Retrieve the chat profile for the given UUID or try to load it.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(UUID uuid) {
        return tryGetProfile(uuid, null);
    }

    /**
     * Retrieve the chat profile for the given UUID in the specified world or try to load it.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(UUID uuid, @Nullable World world) {
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
    default Optional<ChatProfile> getProfile(OfflinePlayer player) {
        return getProfile(player, null);
    }

    /**
     * Retrieves the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(OfflinePlayer player, @Nullable World world) {
        return getProfile(player.getUniqueId(), world);
    }

    /**
     * Retrieves the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(UUID uuid) {
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
