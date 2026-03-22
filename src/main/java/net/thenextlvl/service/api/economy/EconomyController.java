package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface EconomyController extends Controller {
    /**
     * Formats the specified amount as a string.
     *
     * @param amount the number amount to be formatted
     * @return the formatted amount as a string
     */
    String format(Number amount);

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits used for formatting currency amounts
     */
    int fractionalDigits();

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
     * Loads all accounts asynchronously.
     *
     * @return a {@link CompletableFuture} that, when completed, will provide a {@link Set} of {@link Account} objects representing
     * all the accounts available.
     */
    CompletableFuture<@Unmodifiable Set<Account>> loadAccounts();

    /**
     * Retrieves all the accounts currently available.
     *
     * @return a set of accounts
     */
    @Unmodifiable
    Set<Account> getAccounts();

    /**
     * Retrieve the account for the specified player.
     *
     * @param player the player for whom the account will be retrieved
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(final OfflinePlayer player) {
        return getAccount(player, null);
    }

    /**
     * Retrieve the account for the specified uuid and world.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(final OfflinePlayer player, @Nullable final World world) {
        return getAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieve the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(final UUID uuid) {
        return getAccount(uuid, null);
    }

    /**
     * Retrieve the account for the specified uuid and world.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return an optional containing the account, or empty
     */
    Optional<Account> getAccount(UUID uuid, @Nullable World world);

    /**
     * Retrieve the account for the specified player or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(final OfflinePlayer player) {
        return tryGetAccount(player, null);
    }

    /**
     * Retrieve the account for the specified player and world or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(final OfflinePlayer player, @Nullable final World world) {
        return tryGetAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieve the account for the specified uuid or try to load it.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(final UUID uuid) {
        return tryGetAccount(uuid, null);
    }

    /**
     * Retrieve the account for the specified uuid and world or try to load it.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(final UUID uuid, @Nullable final World world) {
        return getAccount(uuid, world)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(uuid, world));
    }

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a CompletableFuture that will complete with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    default CompletableFuture<Account> createAccount(final OfflinePlayer player) {
        return createAccount(player, null);
    }

    /**
     * Creates an account for the specified player in the specified world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world in which the player's account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    default CompletableFuture<Account> createAccount(final OfflinePlayer player, @Nullable final World world) {
        return createAccount(player.getUniqueId(), world);
    }

    /**
     * Creates an account with the given uuid.
     *
     * @param uuid the uuid of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    default CompletableFuture<Account> createAccount(final UUID uuid) {
        return createAccount(uuid, null);
    }

    /**
     * Creates an account with the given uuid and world.
     *
     * @param uuid  the uuid of the account to be created
     * @param world the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uuid, @Nullable World world);

    /**
     * Loads the account for the specified player asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(final OfflinePlayer player) {
        return loadAccount(player, null);
    }

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(final OfflinePlayer player, @Nullable final World world) {
        return loadAccount(player.getUniqueId(), world);
    }

    /**
     * Loads the account with the specified uuid asynchronously.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(final UUID uuid) {
        return loadAccount(uuid, null);
    }

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Optional<Account>> loadAccount(UUID uuid, @Nullable World world);

    /**
     * Deletes the specified account.
     *
     * @param account the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final Account account) {
        return deleteAccount(account.getOwner(), account.getWorld().orElse(null));
    }

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player) {
        return deleteAccount(player, null);
    }

    /**
     * Deletes the account of the specified player in the specified world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world in which the player's account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player, @Nullable final World world) {
        return deleteAccount(player.getUniqueId(), world);
    }

    /**
     * Deletes the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final UUID uuid) {
        return deleteAccount(uuid, null);
    }

    /**
     * Deletes the account with the specified uuid in the specified world.
     *
     * @param uuid  the uuid of the account to be deleted
     * @param world the world in which the account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid, @Nullable World world);
}
