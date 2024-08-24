package net.thenextlvl.service.api.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface EconomyController {
    /**
     * Retrieves the name associated with the economy controller.
     *
     * @return the name of the economy controller.
     */
    String getName();

    /**
     * Formats the specified amount as a string.
     *
     * @param amount the number amount to be formatted
     * @return the formatted amount as a string
     */
    String format(Number amount);

    /**
     * Retrieves the plural form of the currency name based on the provided locale.
     *
     * @param locale the locale for which to retrieve the plural currency name
     * @return the plural form of the currency name as a string
     */
    String getCurrencyNamePlural(Locale locale);

    /**
     * Retrieves the name of the currency associated with the specified locale.
     *
     * @param locale the locale for which to retrieve the currency name
     * @return the name of the currency as a string
     */
    String getCurrencyNameSingular(Locale locale);

    /**
     * Retrieves the currency symbol associated with the economy controller.
     *
     * @return the currency symbol as a string
     */
    String getCurrencySymbol();

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
     * @param uuid the unique ID of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uuid);

    /**
     * Creates an account with the given unique ID and world.
     *
     * @param uuid  the unique ID of the account to be created
     * @param world the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uuid, World world);

    /**
     * Retrieves the account for the specified player.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(OfflinePlayer player);

    /**
     * Retrieves the account for the specified unique ID and world.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(OfflinePlayer player, World world);

    /**
     * Retrieves the account with the specified unique ID.
     *
     * @param uuid the unique ID of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(UUID uuid);

    /**
     * Retrieves the account for the specified unique ID and world.
     *
     * @param uuid  the unique ID of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> getAccount(UUID uuid, World world);

    /**
     * Deletes the specified account.
     *
     * @param account the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(Account account);

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player);

    /**
     * Deletes the account of the specified player in the specified world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world in which the player's account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, World world);

    /**
     * Deletes the account with the specified unique ID.
     *
     * @param uuid the unique ID of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid);

    /**
     * Deletes the account with the specified unique ID in the specified world.
     *
     * @param uuid  the unique ID of the account to be deleted
     * @param world the world in which the account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid, World world);

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits used for formatting currency amounts
     */
    int fractionalDigits();
}
