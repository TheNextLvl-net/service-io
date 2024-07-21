package net.thenextlvl.services.economy.account;

import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface AccountController {
    /**
     * Creates an account with the given unique ID.
     *
     * @param uniqueId the unique ID of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uniqueId);

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player);

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
     * Deletes the account with the specified unique ID.
     *
     * @param uniqueId the unique ID of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Void> deleteAccount(UUID uniqueId);
}
