package net.thenextlvl.service.api.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * EconomyController is an interface that provides methods for managing and interacting
 * with economic systems, such as currency formatting, account retrieval, and multi-currency support.
 */
@NullMarked
public interface EconomyController extends Controller, CurrencyHolder {
    /**
     * Retrieves the plural form of the currency name based on the provided locale.
     *
     * @param locale the locale for which to retrieve the plural currency name
     * @return the plural form of the currency name as a string
     * @deprecated use {@link Currency#getDisplayNamePlural(Locale)}
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default String getCurrencyNamePlural(Locale locale) {
        return PlainTextComponentSerializer.plainText().serialize(getDefaultCurrency().getDisplayNamePlural(locale));
    }

    /**
     * Retrieves the name of the currency associated with the specified locale.
     *
     * @param locale the locale for which to retrieve the currency name
     * @return the name of the currency as a string
     * @deprecated use {@link Currency#getDisplayNameSingular(Locale)}
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default String getCurrencyNameSingular(Locale locale) {
        return PlainTextComponentSerializer.plainText().serialize(getDefaultCurrency().getDisplayNameSingular(locale));
    }

    /**
     * Retrieves the currency symbol associated with the economy controller.
     *
     * @return the currency symbol as a string
     * @deprecated use {@link Currency#getSymbol()}
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default String getCurrencySymbol() {
        return PlainTextComponentSerializer.plainText().serialize(getDefaultCurrency().getSymbol());
    }

    /**
     * Loads all accounts.
     *
     * @return a {@link CompletableFuture} that completes with an unmodifiable {@link Set} of {@link Account} objects
     * representing all available accounts
     */
    default CompletableFuture<@Unmodifiable Set<Account>> loadAccounts() {
        return loadAccounts(null);
    }

    /**
     * Loads all accounts associated with the specified world.
     *
     * @param world the world for which the accounts are to be loaded
     * @return a {@link CompletableFuture} that completes with an unmodifiable {@link Set} of {@link Account} objects
     * representing all available accounts
     */
    CompletableFuture<@Unmodifiable Set<Account>> loadAccounts(@Nullable World world);

    /**
     * Retrieves all the accounts that are currently loaded.
     *
     * @return an unmodifiable set of accounts
     */
    default @Unmodifiable Set<Account> getAccounts() {
        return getAccounts(null);
    }

    /**
     * Retrieves all the accounts associated with the specified world that are currently loaded.
     *
     * @param world the world for which the accounts are to be retrieved
     * @return an unmodifiable set of accounts for the given world
     */
    @Unmodifiable
    Set<Account> getAccounts(@Nullable World world);

    /**
     * Retrieve the account for the specified player.
     *
     * @param player the player for whom the account will be retrieved
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(OfflinePlayer player) {
        return getAccount(player, null);
    }

    /**
     * Retrieve the account for the specified uuid and world.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(OfflinePlayer player, @Nullable World world) {
        return getAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieve the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return an optional containing the account, or empty
     */
    default Optional<Account> getAccount(UUID uuid) {
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
    default CompletableFuture<Optional<Account>> tryGetAccount(OfflinePlayer player) {
        return tryGetAccount(player, null);
    }

    /**
     * Retrieve the account for the specified player and world or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(OfflinePlayer player, @Nullable World world) {
        return tryGetAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieve the account for the specified uuid or try to load it.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(UUID uuid) {
        return tryGetAccount(uuid, null);
    }

    /**
     * Retrieve the account for the specified uuid and world or try to load it.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(UUID uuid, @Nullable World world) {
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
    @Contract("_ -> new")
    default CompletableFuture<Account> createAccount(OfflinePlayer player) {
        return createAccount(player, null);
    }

    /**
     * Creates an account for the specified player in the specified world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world in which the player's account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    @Contract("_, _ -> new")
    default CompletableFuture<Account> createAccount(OfflinePlayer player, @Nullable World world) {
        return createAccount(player.getUniqueId(), world);
    }

    /**
     * Creates an account with the given uuid.
     *
     * @param uuid the uuid of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    @Contract("_ -> new")
    default CompletableFuture<Account> createAccount(UUID uuid) {
        return createAccount(uuid, null);
    }

    /**
     * Creates an account with the given uuid and world.
     *
     * @param uuid  the uuid of the account to be created
     * @param world the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    @Contract("_, _ -> new")
    CompletableFuture<Account> createAccount(UUID uuid, @Nullable World world);

    /**
     * Loads the account for the specified player asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player) {
        return loadAccount(player, null);
    }

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player, @Nullable World world) {
        return loadAccount(player.getUniqueId(), world);
    }

    /**
     * Loads the account with the specified uuid asynchronously.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(UUID uuid) {
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
    default CompletableFuture<Boolean> deleteAccount(Account account) {
        return deleteAccount(account.getOwner(), account.getWorld().orElse(null));
    }

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(OfflinePlayer player) {
        return deleteAccount(player, null);
    }

    /**
     * Deletes the account of the specified player in the specified world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world in which the player's account exists
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, @Nullable World world) {
        return deleteAccount(player.getUniqueId(), world);
    }

    /**
     * Deletes the account with the specified uuid.
     *
     * @param uuid the uuid of the account to be deleted
     * @return a CompletableFuture that will complete when the account is deleted
     */
    default CompletableFuture<Boolean> deleteAccount(UUID uuid) {
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

    /**
     * Determines whether the controller supports handling of multiple worlds.
     *
     * @return {@code true} if multi-world economy is supported, otherwise {@code false}
     * @implSpec If multiple worlds are not supported,
     * implementations must ignore world-specific parameters and only handle cases where the world parameter is null.
     */
    @Contract(pure = true)
    boolean hasMultiWorldSupport();
}
