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
    Optional<Account> getAccount(OfflinePlayer player);

    /**
     * Retrieves a cached account for the specified player in the given world.
     *
     * @param player the player whose account is being retrieved
     * @param world  the world scope of the account
     * @return an optional containing the account, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    Optional<Account> getAccount(OfflinePlayer player, World world);

    /**
     * Retrieves a cached account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return an optional containing the account, or empty if not cached
     */
    default Optional<Account> getAccount(final UUID uuid) {
        return getAccount(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Retrieves a cached account with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return an optional containing the account, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    default Optional<Account> getAccount(final UUID uuid, final World world) {
        return getAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Loads all accounts from the backing store and caches them.
     *
     * @return a future that completes with an unmodifiable set of all accounts
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Account>> resolveAccounts();

    /**
     * Retrieves the account for the specified player, loading from the backing store if not cached.
     *
     * @param player the player whose account is being resolved
     * @return a future that completes with the account, or empty if it does not exist
     * @since 3.0.0
     */
    CompletableFuture<Optional<Account>> resolveAccount(OfflinePlayer player);

    /**
     * Retrieves the account for the specified player in the given world, loading if not cached.
     *
     * @param player the player whose account is being resolved
     * @param world  the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    CompletableFuture<Optional<Account>> resolveAccount(OfflinePlayer player, World world);

    /**
     * Retrieves the account with the specified UUID, loading from the backing store if not cached.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the account, or empty if it does not exist
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final UUID uuid) {
        return resolveAccount(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Retrieves the account with the specified UUID in the given world, loading if not cached.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final UUID uuid, final World world) {
        return resolveAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player);

    /**
     * Creates an account for the specified player in the given world.
     *
     * @param player the player for whom the account will be created
     * @param world  the world scope of the account
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player, World world);

    /**
     * Creates an account with the given UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     */
    default CompletableFuture<Account> createAccount(final UUID uuid) {
        return createAccount(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Creates an account with the given UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    default CompletableFuture<Account> createAccount(final UUID uuid, final World world) {
        return createAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

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
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player);

    /**
     * Deletes the account of the specified player in the given world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world scope of the account
     * @return a future that completes with {@code true} if the account was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, World world);

    /**
     * Deletes the account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with {@code true} if the account was deleted
     */
    default CompletableFuture<Boolean> deleteAccount(final UUID uuid) {
        return deleteAccount(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Deletes the account with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with {@code true} if the account was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    default CompletableFuture<Boolean> deleteAccount(final UUID uuid, final World world) {
        return deleteAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }
}
