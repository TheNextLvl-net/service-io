package net.thenextlvl.service.api.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface EconomyController {
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
     * Retrieve the account for the specified player.
     *
     * @param player the player for whom the account will be retrieved
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(OfflinePlayer player) {
        return getAccount(player.getUniqueId());
    }

    /**
     * Retrieve the account for the specified uuid and world.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(OfflinePlayer player, World world) {
        return getAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieve the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return an optional containing the account, or empty
     */
    Optional<Account> getAccount(UUID uuid);

    /**
     * Retrieve the account for the specified uuid and world.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return an optional containing the account, or empty
     */
    Optional<Account> getAccount(UUID uuid, World world);

    /**
     * Retrieve the account for the specified player or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> tryGetAccount(OfflinePlayer player) {
        return getAccount(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadAccount(player));
    }

    /**
     * Retrieve the account for the specified player and world or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> tryGetAccount(OfflinePlayer player, World world) {
        return getAccount(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadAccount(player, world));
    }

    /**
     * Retrieve the account for the specified uuid or try to load it.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> tryGetAccount(UUID uuid) {
        return getAccount(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadAccount(uuid));
    }

    /**
     * Retrieve the account for the specified uuid and world or try to load it.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> tryGetAccount(UUID uuid, World world) {
        return getAccount(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadAccount(uuid, world));
    }

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a CompletableFuture that will complete with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    default CompletableFuture<Account> createAccount(OfflinePlayer player) throws IllegalStateException {
        return createAccount(player.getUniqueId());
    }

    /**
     * Creates an account for the specified player in the specified world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world in which the player's account will be created
     * @return a CompletableFuture that will complete with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    default CompletableFuture<Account> createAccount(OfflinePlayer player, World world) throws IllegalStateException {
        return createAccount(player.getUniqueId(), world);
    }

    /**
     * Creates an account with the given uuid.
     *
     * @param uuid the uuid of the account to be created
     * @return a CompletableFuture that will complete with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    CompletableFuture<Account> createAccount(UUID uuid) throws IllegalStateException;

    /**
     * Creates an account with the given uuid and world.
     *
     * @param uuid  the uuid of the account to be created
     * @param world the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    CompletableFuture<Account> createAccount(UUID uuid, World world) throws IllegalStateException;

    /**
     * Loads the account for the specified player asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> loadAccount(OfflinePlayer player) {
        return loadAccount(player.getUniqueId());
    }

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Account> loadAccount(OfflinePlayer player, World world) {
        return loadAccount(player.getUniqueId(), world);
    }

    /**
     * Loads the account with the specified uuid asynchronously.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> loadAccount(UUID uuid);

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Account> loadAccount(UUID uuid, World world);

    /**
     * Deletes the specified account.
     *
     * @param account the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(Account account) {
        return account.getWorld()
                .map(world -> deleteAccount(account.getOwner(), world))
                .orElseGet(() -> deleteAccount(account.getOwner()));
    }

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(OfflinePlayer player) {
        return deleteAccount(player.getUniqueId());
    }

    /**
     * Deletes the account of the specified player in the specified world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world in which the player's account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, World world) {
        return deleteAccount(player.getUniqueId(), world);
    }

    /**
     * Deletes the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid);

    /**
     * Deletes the account with the specified uuid in the specified world.
     *
     * @param uuid  the uuid of the account to be deleted
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

    /**
     * Retrieves the name associated with the economy controller.
     *
     * @return the name of the economy controller.
     */
    @Contract(pure = true)
    String getName();
}
