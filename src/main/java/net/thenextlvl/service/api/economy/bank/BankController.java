package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.capability.CapabilityException;
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
 * Controller for managing bank accounts.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
 */
public interface BankController extends Controller {
    /**
     * Retrieves the currency controller for managing currencies.
     *
     * @return the currency controller
     */
    @Contract(pure = true)
    CurrencyController getCurrencyController();

    /**
     * Retrieves all currently cached banks.
     *
     * @return an unmodifiable set of cached banks
     */
    @Unmodifiable
    Set<Bank> getBanks();

    /**
     * Retrieves all currently cached banks in the specified world.
     *
     * @param world the world to filter by
     * @return an unmodifiable set of cached banks
     * @throws CapabilityException if multi-world is not supported
     */
    @Unmodifiable
    Set<Bank> getBanks(World world);

    /**
     * Retrieves a cached bank with the specified name.
     *
     * @param name the name of the bank
     * @return an optional containing the bank, or empty if not cached
     */
    Optional<Bank> getBank(String name);

    /**
     * Retrieves a cached bank for the specified player.
     *
     * @param player the player whose bank is being retrieved
     * @return an optional containing the bank, or empty if not cached
     */
    default Optional<Bank> getBank(final OfflinePlayer player) {
        return getBank(player.getUniqueId());
    }

    /**
     * Retrieves a cached bank for the specified player in the given world.
     *
     * @param player the player whose bank is being retrieved
     * @param world  the world scope of the bank
     * @return an optional containing the bank, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    default Optional<Bank> getBank(final OfflinePlayer player, final World world) {
        return getBank(player.getUniqueId(), world);
    }

    /**
     * Retrieves a cached bank owned by the specified player.
     *
     * @param uuid the UUID of the bank owner
     * @return an optional containing the bank, or empty if not cached
     */
    Optional<Bank> getBank(UUID uuid);

    /**
     * Retrieves a cached bank owned by the specified player in the given world.
     *
     * @param uuid  the UUID of the bank owner
     * @param world the world scope of the bank
     * @return an optional containing the bank, or empty if not cached
     * @throws CapabilityException if multi-world is not supported
     */
    Optional<Bank> getBank(UUID uuid, World world);

    /**
     * Loads all banks from the backing store and caches them.
     *
     * @return a future that completes with an unmodifiable set of all banks
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Bank>> resolveBanks();

    /**
     * Loads all banks in the specified world from the backing store and caches them.
     *
     * @param world the world from which to load banks
     * @return a future that completes with an unmodifiable set of banks in the world
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Bank>> resolveBanks(World world);

    /**
     * Retrieves the bank with the specified name, loading from the backing store if not cached.
     *
     * @param name the name of the bank
     * @return a future that completes with the bank, or empty if it does not exist
     * @since 3.0.0
     */
    CompletableFuture<Optional<Bank>> resolveBank(String name);

    /**
     * Retrieves the bank for the specified player, loading if not cached.
     *
     * @param player the player whose bank is being resolved
     * @return a future that completes with the bank, or empty if it does not exist
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Bank>> resolveBank(final OfflinePlayer player) {
        return resolveBank(player.getUniqueId());
    }

    /**
     * Retrieves the bank for the specified player in the given world, loading if not cached.
     *
     * @param player the player whose bank is being resolved
     * @param world  the world scope of the bank
     * @return a future that completes with the bank, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    default CompletableFuture<Optional<Bank>> resolveBank(final OfflinePlayer player, final World world) {
        return resolveBank(player.getUniqueId(), world);
    }

    /**
     * Retrieves the bank owned by the specified player, loading if not cached.
     *
     * @param uuid the UUID of the bank owner
     * @return a future that completes with the bank, or empty if it does not exist
     * @since 3.0.0
     */
    CompletableFuture<Optional<Bank>> resolveBank(UUID uuid);

    /**
     * Retrieves the bank owned by the specified player in the given world, loading if not cached.
     *
     * @param uuid  the UUID of the bank owner
     * @param world the world scope of the bank
     * @return a future that completes with the bank, or empty if it does not exist
     * @throws CapabilityException if multi-world is not supported
     * @since 3.0.0
     */
    CompletableFuture<Optional<Bank>> resolveBank(UUID uuid, World world);

    /**
     * Creates a bank for the specified player with the given name.
     *
     * @param player the owner of the bank
     * @param name   the unique name of the bank
     * @return a future that completes with the created bank
     * @throws IllegalStateException if a bank with that owner or name already exists
     */
    @Contract("_, _ -> new")
    default CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name) {
        return createBank(player.getUniqueId(), name);
    }

    /**
     * Creates a bank for the specified player in the given world.
     *
     * @param player the owner of the bank
     * @param name   the unique name of the bank
     * @param world  the world scope of the bank
     * @return a future that completes with the created bank
     * @throws IllegalStateException if a bank with that owner or name already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    @Contract("_, _, _ -> new")
    default CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name, final World world) {
        return createBank(player.getUniqueId(), name, world);
    }

    /**
     * Creates a bank for the specified owner with the given name.
     *
     * @param uuid the UUID of the bank owner
     * @param name the unique name of the bank
     * @return a future that completes with the created bank
     * @throws IllegalStateException if a bank with that owner or name already exists
     */
    @Contract("_, _ -> new")
    CompletableFuture<Bank> createBank(UUID uuid, String name);

    /**
     * Creates a bank for the specified owner with the given name in the given world.
     *
     * @param uuid  the UUID of the bank owner
     * @param name  the unique name of the bank
     * @param world the world scope of the bank
     * @return a future that completes with the created bank
     * @throws IllegalStateException if a bank with that owner or name already exists
     * @throws CapabilityException   if multi-world is not supported
     */
    @Contract("_, _, _ -> new")
    CompletableFuture<Bank> createBank(UUID uuid, String name, World world);

    /**
     * Deletes the specified bank.
     *
     * @param bank the bank to delete
     * @return a future that completes with {@code true} if the bank was deleted
     */
    default CompletableFuture<Boolean> deleteBank(final Bank bank) {
        return bank.getWorld()
                .map(world -> deleteBank(bank.getOwner(), world))
                .orElseGet(() -> deleteBank(bank.getOwner()));
    }

    /**
     * Deletes the bank of the specified player.
     *
     * @param player the player whose bank will be deleted
     * @return a future that completes with {@code true} if the bank was deleted
     */
    default CompletableFuture<Boolean> deleteBank(final OfflinePlayer player) {
        return deleteBank(player.getUniqueId());
    }

    /**
     * Deletes the bank of the specified player in the given world.
     *
     * @param player the player whose bank will be deleted
     * @param world  the world scope of the bank
     * @return a future that completes with {@code true} if the bank was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    default CompletableFuture<Boolean> deleteBank(final OfflinePlayer player, final World world) {
        return deleteBank(player.getUniqueId(), world);
    }

    /**
     * Deletes the bank with the specified name.
     *
     * @param name the name of the bank to delete
     * @return a future that completes with {@code true} if the bank was deleted
     */
    CompletableFuture<Boolean> deleteBank(String name);

    /**
     * Deletes the bank owned by the specified player.
     *
     * @param uuid the UUID of the bank owner
     * @return a future that completes with {@code true} if the bank was deleted
     */
    CompletableFuture<Boolean> deleteBank(UUID uuid);

    /**
     * Deletes the bank owned by the specified player in the given world.
     *
     * @param uuid  the UUID of the bank owner
     * @param world the world scope of the bank
     * @return a future that completes with {@code true} if the bank was deleted
     * @throws CapabilityException if multi-world is not supported
     */
    CompletableFuture<Boolean> deleteBank(UUID uuid, World world);
}
