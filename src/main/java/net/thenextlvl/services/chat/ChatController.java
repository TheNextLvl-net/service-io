package net.thenextlvl.services.chat;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The ChatController interface provides methods to retrieve a chat profile of a player.
 */
public interface ChatController {
    /**
     * Retrieves the chat profile for the given OfflinePlayer.
     *
     * @param player The OfflinePlayer whose ChatProfile is to be retrieved.
     * @return a CompletableFuture that will complete with the chat profile
     */
    CompletableFuture<ChatProfile> getProfile(OfflinePlayer player);

    /**
     * Retrieves the chat profile for the given OfflinePlayer in the specified world.
     *
     * @param player The OfflinePlayer whose chat profile is to be retrieved.
     * @param world  The world for which the chat profile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> getProfile(OfflinePlayer player, World world);

    /**
     * Retrieves the chat profile for the given UUID.
     *
     * @param uuid The UUID of the player whose ChatProfile is to be retrieved.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> getProfile(UUID uuid);

    /**
     * Retrieves the chat profile for the given UUID in the specified world.
     *
     * @param uuid  The UUID of the player whose ChatProfile is to be retrieved.
     * @param world The world for which the ChatProfile is requested.
     * @return A CompletableFuture that will complete with the chat profile.
     */
    CompletableFuture<ChatProfile> getProfile(UUID uuid, World world);
}
