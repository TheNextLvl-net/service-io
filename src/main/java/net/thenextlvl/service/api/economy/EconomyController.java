package net.thenextlvl.service.api.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface EconomyController {
    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player);

    /**
     * Creates an account for the specified player in the specified world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world in which the player's account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player, World world);

    /**
     * Creates an account with the given unique ID.
     *
     * @param uniqueId the unique ID of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uniqueId);

    /**
     * Creates an account with the given unique ID and world.
     *
     * @param uniqueId the unique ID of the account to be created
     * @param world    the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uniqueId, World world);

    /**
     * Retrieves the account for the specified player.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(OfflinePlayer player);

    /**
     * Retrieves the account with the specified unique ID.
     *
     * @param uniqueId the unique ID of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(UUID uniqueId);

    /**
     * Retrieves the account for the specified unique ID and world.
     *
     * @param uniqueId the unique ID of the account to be retrieved
     * @param world    the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(UUID uniqueId, World world);

    /**
     * Deletes the specified account.
     *
     * @param account the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(Account account);

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(OfflinePlayer player);

    /**
     * Deletes the account of the specified player in the specified world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world in which the player's account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(OfflinePlayer player, World world);

    /**
     * Deletes the account with the specified unique ID.
     *
     * @param uniqueId the unique ID of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(UUID uniqueId);

    /**
     * Deletes the account with the specified unique ID in the specified world.
     *
     * @param uniqueId the unique ID of the account to be deleted
     * @param world    the world in which the account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(UUID uniqueId, World world);
}
