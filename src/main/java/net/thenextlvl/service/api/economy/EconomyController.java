package net.thenextlvl.service.api.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

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
    default CompletableFuture<Optional<Account>> tryGetAccount(OfflinePlayer player) {
        return getAccount(player)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(player));
    }

    /**
     * Retrieve the account for the specified player and world or try to load it.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(OfflinePlayer player, World world) {
        return getAccount(player, world)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(player, world));
    }

    /**
     * Retrieve the account for the specified uuid or try to load it.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(UUID uuid) {
        return getAccount(uuid)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(uuid));
    }

    /**
     * Retrieve the account for the specified uuid and world or try to load it.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> tryGetAccount(UUID uuid, World world) {
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
    default CompletableFuture<Account> createAccount(OfflinePlayer player) {
        return createAccount(player.getUniqueId());
    }

    /**
     * Creates an account for the specified player in the specified world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world in which the player's account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    default CompletableFuture<Account> createAccount(OfflinePlayer player, World world) {
        return createAccount(player.getUniqueId(), world);
    }

    /**
     * Creates an account with the given uuid.
     *
     * @param uuid the uuid of the account to be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uuid);

    /**
     * Creates an account with the given uuid and world.
     *
     * @param uuid  the uuid of the account to be created
     * @param world the world in which the account will be created
     * @return a CompletableFuture that will complete with the created account
     */
    CompletableFuture<Account> createAccount(UUID uuid, World world);

    /**
     * Loads the account for the specified player asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player) {
        return loadAccount(player.getUniqueId());
    }

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param player the player for whom the account will be retrieved
     * @param world  the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    default CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player, World world) {
        return loadAccount(player.getUniqueId(), world);
    }

    /**
     * Loads the account with the specified uuid asynchronously.
     *
     * @param uuid the uuid of the account to be retrieved
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Optional<Account>> loadAccount(UUID uuid);

    /**
     * Loads the account for the specified uuid and world asynchronously.
     *
     * @param uuid  the uuid of the account to be retrieved
     * @param world the world in which the account is located
     * @return a CompletableFuture that will complete with the retrieved account
     */
    CompletableFuture<Optional<Account>> loadAccount(UUID uuid, World world);

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
}
