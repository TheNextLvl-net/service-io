package net.thenextlvl.service.economy;

import net.thenextlvl.service.Controller;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.capability.CapabilityProvider;
import net.thenextlvl.service.economy.currency.CurrencyController;
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
 * @since 1.0.0
 */
public interface EconomyController extends Controller, CapabilityProvider<EconomyCapability> {
    /**
     * Retrieves the currency controller for managing currencies.
     *
     * @return the currency controller
     * @since 3.0.0
     */
    @Contract(pure = true)
    CurrencyController getCurrencyController();

    /**
     * Retrieves all currently cached accounts.
     * <p>
     * This method returns immediately without performing any I/O.
     *
     * @return an unmodifiable set of cached accounts
     * @since 2.2.0
     */
    @Unmodifiable
    Set<Account> getAccounts();

    /**
     * Retrieves all currently cached accounts from the specified world.
     * <p>
     * This method returns immediately without performing any I/O.
     *
     * @param world the world to get all accounts from
     * @return an unmodifiable set of cached accounts
     * @since 3.0.0
     */
    @Unmodifiable
    Set<Account> getAccounts(World world);

    /**
     * Retrieves a cached account for the specified player.
     *
     * @param player the player whose account is being retrieved
     * @return an optional containing the account, or empty if not cached
     * @since 3.0.0
     */
    Optional<Account> getAccount(OfflinePlayer player);

    /**
     * Retrieves a cached account for the specified player in the given world.
     *
     * @param player the player whose account is being retrieved
     * @param world  the world scope of the account
     * @return an optional containing the account, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    Optional<Account> getAccount(OfflinePlayer player, World world);

    /**
     * Retrieves a cached account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return an optional containing the account, or empty if not cached
     * @since 3.0.0
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
     * @since 3.0.0
     */
    default Optional<Account> getAccount(final UUID uuid, final World world) {
        return getAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Loads all accounts from the backing store.
     *
     * @return a future that completes with an unmodifiable set of all accounts
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Account>> loadAccounts();

    /**
     * Loads all accounts from the backing store.
     *
     * @param world the world from which to load all accounts
     * @return a future that completes with an unmodifiable set of all accounts
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Account>> loadAccounts(World world);

    /**
     * Retrieves the account for the specified player, loading from the backing store if not cached.
     *
     * @param player the player whose account is being resolved
     * @return a future that completes with the account, or empty if it does not exist
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final OfflinePlayer player) {
        return getAccount(player)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(player));
    }

    /**
     * Retrieves the account for the specified player in the given world, loading if not cached.
     *
     * @param player the player whose account is being resolved
     * @param world  the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> resolveAccount(final OfflinePlayer player, final World world) {
        return getAccount(player, world)
                .map(account -> CompletableFuture.completedFuture(Optional.of(account)))
                .orElseGet(() -> loadAccount(player, world));
    }

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
     * Loads the account for the specified player from the backing store.
     *
     * @param player the player whose account is being loaded
     * @return a future that completes with the account, or empty if it does not exist
     * @since 3.0.0
     */
    CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player);

    /**
     * Loads the account for the specified player in the given world from the backing store.
     *
     * @param player the player whose account is being loaded
     * @param world  the world scope of the account
     * @return a future that completes with the account, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player, World world);

    /**
     * Loads the account with the specified UUID from the backing store.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the account, or empty if it does not exist
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> loadAccount(final UUID uuid) {
        return loadAccount(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Loads the account with the specified UUID in the given world from the backing store.
     *
     * @param uuid  the UUID of the account owner
     * @param world the world scope of the account
     * @return a future that completes with the account, or empty if it does not exists
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Account>> loadAccount(final UUID uuid, final World world) {
        return loadAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Creates an account for the specified player.
     *
     * @param player the player for whom the account will be created
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @since 3.0.0
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
     * @since 3.0.0
     */
    CompletableFuture<Account> createAccount(OfflinePlayer player, World world);

    /**
     * Creates an account with the given UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with the created account
     * @throws IllegalStateException if a similar account already exists
     * @since 3.0.0
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
     * @since 3.0.0
     */
    default CompletableFuture<Account> createAccount(final UUID uuid, final World world) {
        return createAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Deletes the specified account.
     *
     * @param account the account to delete
     * @return a future that completes with {@code true} if the account was deleted
     * @since 3.0.0
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
     * @since 3.0.0
     */
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player);

    /**
     * Deletes the account of the specified player in the given world.
     *
     * @param player the player whose account will be deleted
     * @param world  the world scope of the account
     * @return a future that completes with {@code true} if the account was deleted
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, World world);

    /**
     * Deletes the account with the specified UUID.
     *
     * @param uuid the UUID of the account owner
     * @return a future that completes with {@code true} if the account was deleted
     * @since 3.0.0
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
     * @since 3.0.0
     */
    default CompletableFuture<Boolean> deleteAccount(final UUID uuid, final World world) {
        return deleteAccount(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }
}
