package net.thenextlvl.service.api.chat;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The ChatController interface provides methods to retrieve a chat profile of a player.
 */
@NullMarked
public interface ChatController {
    /**
     * Loads the chat profile for the given OfflinePlayer.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    default CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player) {
        return loadProfile(player.getUniqueId());
    }

    /**
     * Loads the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, World world) {
        return loadProfile(player.getUniqueId(), world);
    }

    /**
     * Loads the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> loadProfile(UUID uuid);

    /**
     * Loads the chat profile for the given UUID in the specified world.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> loadProfile(UUID uuid, World world);

    /**
     * Retrieves the chat profile for the given OfflinePlayer or try to load it.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    default CompletableFuture<ChatProfile> tryGetProfile(OfflinePlayer player) {
        return getProfile(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadProfile(player));
    }

    /**
     * Retrieve the chat profile for the given OfflinePlayer in the specified world or try to load it.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(OfflinePlayer player, World world) {
        return getProfile(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadProfile(player, world));
    }

    /**
     * Retrieve the chat profile for the given UUID or try to load it.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(UUID uuid) {
        return getProfile(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadProfile(uuid));
    }

    /**
     * Retrieve the chat profile for the given UUID in the specified world or try to load it.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    default CompletableFuture<ChatProfile> tryGetProfile(UUID uuid, World world) {
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
        return getProfile(player.getUniqueId());
    }

    /**
     * Retrieves the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return an optional containing the chat profile, or empty.
     */
    default Optional<ChatProfile> getProfile(OfflinePlayer player, World world) {
        return getProfile(player.getUniqueId(), world);
    }

    /**
     * Retrieves the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return an optional containing the chat profile, or empty.
     */
    Optional<ChatProfile> getProfile(UUID uuid);

    /**
     * Retrieves the chat profile for the given UUID in the specified world.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return an optional containing the chat profile, or empty.
     */
    Optional<ChatProfile> getProfile(UUID uuid, World world);

    /**
     * Retrieves the name associated with the permission controller.
     *
     * @return the name of the chat controller.
     */
    @Contract(pure = true)
    String getName();
}
