package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public interface BankController extends Controller, CurrencyHolder {
    /**
     * Creates a bank for the specified player with the given name.
     * <p>
     * Completes with an {@link IllegalStateException} if a similar bank already exists
     *
     * @param player the owner of the bank (must be unique)
     * @param name   the name of the bank (must be unique)
     * @return a CompletableFuture that completes with the created bank
     */
    @Contract("_, _ -> new")
    default CompletableFuture<Bank> createBank(OfflinePlayer player, String name) {
        return createBank(player.getUniqueId(), name);
    }

    /**
     * Creates a bank asynchronously with the specified player, name, and world.
     * <p>
     * Completes with an {@link IllegalStateException} if a similar bank already exists
     *
     * @param player the owner of the bank (must be unique)
     * @param name   the name of the bank (must be unique)
     * @param world  the world in which the bank is located
     * @return a CompletableFuture that completes with the created bank
     */
    @Contract("_, _, _ -> new")
    default CompletableFuture<Bank> createBank(OfflinePlayer player, String name, @Nullable World world) {
        return createBank(player.getUniqueId(), name, world);
    }

    /**
     * Creates a bank asynchronously with the specified UUID and name.
     * <p>
     * Completes with an {@link IllegalStateException} if a similar bank already exists
     *
     * @param uuid the UUID of the bank (must be unique)
     * @param name the name of the bank (must be unique)
     * @return a CompletableFuture that completes with the created bank
     */
    CompletableFuture<Bank> createBank(UUID uuid, String name);
    @Contract("_, _ -> new")

    /**
     * Creates a new bank with the provided UUID, name, and world.
     * <p>
     * Completes with an {@link IllegalStateException} if a similar bank already exists
     *
     * @param uuid  the UUID of the bank (must be unique)
     * @param name  the name of the bank (must be unique)
     * @param world the world in which the bank exists
     * @return a CompletableFuture that completes with the created bank
     */
    CompletableFuture<Bank> createBank(UUID uuid, String name, World world);
    @Contract("_, _, _ -> new")

    /**
     * Loads a bank asynchronously with the specified player.
     *
     * @param player the player for whom the bank should be loaded
     * @return a CompletableFuture that completes with the loaded bank
     */
    default CompletableFuture<Bank> loadBank(OfflinePlayer player) {
        return loadBank(player.getUniqueId());
    }

    /**
     * Loads a bank asynchronously with the specified player and world.
     *
     * @param player the player for whom the bank should be loaded
     * @param world  the world in which the bank is located
     * @return a CompletableFuture that completes with the loaded bank
     */
    default CompletableFuture<Bank> loadBank(OfflinePlayer player, World world) {
        return loadBank(player.getUniqueId(), world);
    }

    /**
     * Loads a bank asynchronously with the specified name.
     *
     * @param name the name of the bank to be loaded
     * @return a CompletableFuture that completes with the loaded bank
     */
    CompletableFuture<Bank> loadBank(String name);

    /**
     * Loads a bank asynchronously with the specified UUID.
     *
     * @param uuid the UUID of the bank
     * @return a CompletableFuture that completes with the loaded bank
     */
    CompletableFuture<Bank> loadBank(UUID uuid);

    /**
     * Loads a bank asynchronously with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank
     * @param world the world in which the bank is located
     * @return a CompletableFuture that completes with the loaded bank
     */
    CompletableFuture<Bank> loadBank(UUID uuid, World world);

    /**
     * Retrieves a Set of all banks.
     *
     * @return a CompletableFuture that completes with a Set of all banks
     */
    CompletableFuture<@Unmodifiable Set<Bank>> loadBanks();

    /**
     * Retrieves a set of all banks in the specified world.
     *
     * @param world the world from which to retrieve the banks
     * @return a CompletableFuture that completes with a Set of banks in the specified world
     */
    CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(World world);

    /**
     * Tries to retrieve the {@link Bank} with the specified name.
     *
     * @param name the name of the bank being retrieved
     * @return a {@code CompletableFuture<Bank>} that completes with an {@code Optional<Bank>} containing the bank
     * associated with the name, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(String name) {
        return getBank(name)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(name));
    }

    /**
     * Tries to retrieve the bank associated with the specified player.
     *
     * @param player the player whose bank is being retrieved
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with
     * the player, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(OfflinePlayer player) {
        return getBank(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(player));
    }

    /**
     * Tries to retrieve the bank associated with the specified player and world.
     *
     * @param player the player whose bank is being retrieved
     * @param world  the world the bank belongs to
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with
     * the player and world, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(OfflinePlayer player, World world) {
        return getBank(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(player, world));
    }

    /**
     * Tries to retrieve the bank associated with the specified UUID.
     *
     * @param uuid the UUID of the bank's owner
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with the UUID,
     * or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(UUID uuid) {
        return getBank(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(uuid));
    }

    /**
     * Tries to retrieve the bank associated with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank's owner
     * @param world the world the bank belongs to
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with
     * the UUID and world, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(UUID uuid, World world) {
        return getBank(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(uuid, world));
    }

    /**
     * Deletes a bank associated with the specified player.
     *
     * @param bank the bank to be deleted
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    default CompletableFuture<Boolean> deleteBank(Bank bank) {
        return bank.getWorld()
                .map(world -> deleteBank(bank.getOwner(), world))
                .orElseGet(() -> deleteBank(bank.getOwner()));
    }

    /**
     * Deletes a bank associated with the specified player.
     *
     * @param player the player whose bank is to be deleted
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    default CompletableFuture<Boolean> deleteBank(OfflinePlayer player) {
        return deleteBank(player.getUniqueId());
    }

    /**
     * Deletes a bank associated with the specified player in the given world.
     *
     * @param player the player whose bank is to be deleted
     * @param world  the world where the bank is located
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    default CompletableFuture<Boolean> deleteBank(OfflinePlayer player, World world) {
        return deleteBank(player.getUniqueId(), world);
    }

    /**
     * Deletes a bank with the specified name.
     *
     * @param name the name of the bank to be deleted
     * @return A CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    CompletableFuture<Boolean> deleteBank(String name);

    /**
     * Deletes a bank with the specified UUID.
     *
     * @param uuid The UUID of the bank to be deleted.
     * @return A CompletableFuture that completes with a boolean value indicating whether the deletion was successful.
     */
    CompletableFuture<Boolean> deleteBank(UUID uuid);

    /**
     * Deletes a bank with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the bank to be deleted
     * @param world the world where the bank is located
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    CompletableFuture<Boolean> deleteBank(UUID uuid, World world);

    /**
     * Retrieves a set of all banks.
     *
     * @return a set of all banks
     */
    @Unmodifiable
    Set<Bank> getBanks();

    /**
     * Retrieves a set of all banks in the {@link World}.
     *
     * @param world the world from which to retrieve the banks
     * @return a {@code Set<Bank>} containing all the banks in the world
     */
    @Unmodifiable
    Set<Bank> getBanks(World world);

    /**
     * Retrieves the {@link Bank} associated with the specified name.
     *
     * @param name the name of the bank being retrieved
     * @return an {@code Optional<Bank>} containing the bank associated with the name, or empty if not found
     */
    Optional<Bank> getBank(String name);

    /**
     * Retrieves the {@link Bank} associated with the specified {@link OfflinePlayer}.
     *
     * @param player the player whose bank is being retrieved
     * @return an {@code Optional<Bank>} containing the bank associated with the player, or empty if not found
     */
    default Optional<Bank> getBank(OfflinePlayer player) {
        return getBank(player.getUniqueId());
    }

    /**
     * Retrieves the {@link Bank} associated with the specified {@link OfflinePlayer} and {@link World}.
     *
     * @param player the player whose bank is being retrieved
     * @param world  the world the bank belongs to
     * @return an {@code Optional<Bank>} containing the bank associated with the player and world, or empty if not found
     */
    default Optional<Bank> getBank(OfflinePlayer player, World world) {
        return getBank(player.getUniqueId(), world);
    }

    /**
     * Retrieves the {@link Bank} associated with the specified UUID.
     *
     * @param uuid the UUID of the bank's owner
     * @return an Optional containing the Bank associated with the UUID, or empty if not found
     */
    Optional<Bank> getBank(UUID uuid);

    /**
     * Retrieves the {@link Bank} associated with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank's owner
     * @param world the world the bank belongs to
     * @return an Optional containing the Bank associated with the UUID and world, or empty if not found
     */
    Optional<Bank> getBank(UUID uuid, World world);
}
