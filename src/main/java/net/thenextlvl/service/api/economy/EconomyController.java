package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.capability.CapabilityProvider;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Central controller for managing player accounts and currencies.
 * <p>
 * Use {@link #getCapabilities()} to discover which optional features the
 * provider supports before calling capability-specific methods.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
 * @since 3.0.0
 */
public interface EconomyController extends Controller, CapabilityProvider<EconomyCapability> {
    /**
     * Retrieves the currency controller for managing currencies.
     *
     * @return the currency controller
     */
    @Contract(pure = true)
    CurrencyController getCurrencyController();

    /**
     * Retrieves all currently cached accounts.
     * <p>
     * This method returns immediately without performing any I/O.
     *
     * @return an unmodifiable set of cached accounts
     */
    @Unmodifiable
    Set<Account> getAccounts();

    /**
     * Retrieves a cached account for the specified player.
     *
     * @param player the player whose account is being retrieved
     * @return an optional containing the account, or empty if not cached
     */
    default Optional<Account> getAccount(final OfflinePlayer player) {
        return getAccount(player.getUniqueId());
    }

    /**
     * Retrieves a cached account for the specified player in the given world.
     *
     * @param player the player whose account is being retrieved
     * @param world  the world scope of the account
     * @return an optional containing the account, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    default Optional<Account> getAccount(final OfflinePlayer player, final World world) {
        return getAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieves a cached account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return an optional containing the account, or empty if not cached
     */
    Optional<Account> getAccount(UUID uuid);

    /**
     * Retrieves a cached account with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return an optional containing the account, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    Optional<Account> getAccount(UUID uuid, World world);

    /**
     * Loads all accounts from the backing store and caches them.
     *
     * @return a future that completes with an unmodifiable set of all accounts
     */
    CompletableFuture<@Unmodifiable Set<Account>> resolveAccounts();

    /**
     * Retrieves the account for the specified player, loading from the backing store if not cached.
     *
     * @param player the player whose account is being resolved
     * @return a future that completes with the account, or empty if it does not exist
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final OfflinePlayer player) {
        return resolveAccount(player.getUniqueId());
    }

    /**
     * Retrieves the account for the specified player in the given world, loading if not cached.
     *
     * @param player the player whose account is being resolved
     * @param world  the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final OfflinePlayer player, final World world) {
        return resolveAccount(player.getUniqueId(), world);
    }

    /**
     * Retrieves the account with the specified UUID, loading from the backing store if not cached.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the account, or empty if it does not exist
     */
    CompletableFuture<Optional<Account>> resolveAccount(UUID uuid);

    /**
     * Retrieves the account with the specified UUID in the given world, loading if not cached.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     */
    CompletableFuture<Optional<Account>> resolveAccount(UUID uuid, World world);

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    default CompletableFuture<Account> createAccount(final OfflinePlayer player) {
        return createAccount(player.getUniqueId());
    }

    /**
     * Creates an account for the specified player in the given world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world scope of the account
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    default CompletableFuture<Account> createAccount(final OfflinePlayer player, final World world) {
        return createAccount(player.getUniqueId(), world);
    }

    /**
     * Creates an account with the given UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    CompletableFuture<Account> createAccount(UUID uuid);

    /**
     * Creates an account with the given UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    CompletableFuture<Account> createAccount(UUID uuid, World world);

    /**
     * Deletes the specified account.
     *
     * @param account the account to delete
     * @return a future that completes with {@code true} if the account was deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final Account account) {
        return account.getWorld()
                .map(world -> deleteAccount(account.getOwner(), world))
                .orElseGet(() -> deleteAccount(account.getOwner()));
    }

    /**
     * Deletes the account of the specified player.
     *
     * @param player the player whose account will be deleted
     * @return a future that completes with {@code true} if the account was deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player) {
        return deleteAccount(player.getUniqueId());
    }

    /**
     * Deletes the account of the specified player in the given world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world scope of the account
     * @return a future that completes with {@code true} if the account was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    default CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player, final World world) {
        return deleteAccount(player.getUniqueId(), world);
    }

    /**
     * Deletes the account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with {@code true} if the account was deleted
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid);

    /**
     * Deletes the account with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with {@code true} if the account was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    CompletableFuture<Boolean> deleteAccount(UUID uuid, World world);
}
