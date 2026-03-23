package net.thenextlvl.service.api.economy.bank;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a controller for managing banks tied to players and worlds.
 *
 * @since 1.0.0
 */
public interface BankController extends Controller {
    /**
     * Retrieves the {@code CurrencyHolder} associated with the economy controller.
     *
     * @return the {@code CurrencyHolder} instance that manages the defined currencies for the controller
     */
    @Contract(pure = true)
    CurrencyHolder getCurrencyHolder();

    /**
     * Formats the specified amount as a string.
     *
     * @param amount the number amount to be formatted
     * @return the formatted amount as a string
     * @deprecated Use {@link Currency#format(Number, Locale)} instead
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default String format(final Number amount) {
        return PlainTextComponentSerializer.plainText().serialize(getCurrencyHolder().getDefaultCurrency().format(amount, Locale.US));
    }

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits used for formatting currency amounts
     * @deprecated Use {@link Currency#getFractionalDigits()} instead
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default int fractionalDigits() {
        return getCurrencyHolder().getDefaultCurrency().getFractionalDigits();
    }

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
    default CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name) {
        return createBank(player, name, null);
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
    default CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name, @Nullable final World world) {
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
    @Contract("_, _ -> new")
    default CompletableFuture<Bank> createBank(final UUID uuid, final String name) {
        return createBank(uuid, name, null);
    }

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
    @Contract("_, _, _ -> new")
    CompletableFuture<Bank> createBank(UUID uuid, String name, @Nullable World world);

    /**
     * Loads a bank asynchronously with the specified player.
     *
     * @param player the player for whom the bank should be loaded
     * @return a CompletableFuture that completes with the loaded bank
     */
    default CompletableFuture<Bank> loadBank(final OfflinePlayer player) {
        return loadBank(player, null);
    }

    /**
     * Loads a bank asynchronously with the specified player and world.
     *
     * @param player the player for whom the bank should be loaded
     * @param world  the world in which the bank is located
     * @return a CompletableFuture that completes with the loaded bank
     */
    default CompletableFuture<Bank> loadBank(final OfflinePlayer player, @Nullable final World world) {
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
    default CompletableFuture<Bank> loadBank(final UUID uuid) {
        return loadBank(uuid, null);
    }

    /**
     * Loads a bank asynchronously with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank
     * @param world the world in which the bank is located
     * @return a CompletableFuture that completes with the loaded bank
     */
    CompletableFuture<Bank> loadBank(UUID uuid, @Nullable World world);

    /**
     * Retrieves a Set of all banks.
     *
     * @return a CompletableFuture that completes with a Set of all banks
     */
    default CompletableFuture<@Unmodifiable Set<Bank>> loadBanks() {
        return loadBanks(null);
    }

    /**
     * Retrieves a set of all banks in the specified world.
     *
     * @param world the world from which to retrieve the banks
     * @return a CompletableFuture that completes with a Set of banks in the specified world
     */
    CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(@Nullable World world);

    /**
     * Tries to retrieve the {@link Bank} with the specified name.
     *
     * @param name the name of the bank being retrieved
     * @return a {@code CompletableFuture<Bank>} that completes with an {@code Optional<Bank>} containing the bank
     * associated with the name, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(final String name) {
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
    default CompletableFuture<Bank> tryGetBank(final OfflinePlayer player) {
        return tryGetBank(player, null);
    }

    /**
     * Tries to retrieve the bank associated with the specified player and world.
     *
     * @param player the player whose bank is being retrieved
     * @param world  the world the bank belongs to
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with
     * the player and world, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(final OfflinePlayer player, @Nullable final World world) {
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
    default CompletableFuture<Bank> tryGetBank(final UUID uuid) {
        return tryGetBank(uuid, null);
    }

    /**
     * Tries to retrieve the bank associated with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank's owner
     * @param world the world the bank belongs to
     * @return a CompletableFuture that completes with an Optional containing the Bank associated with
     * the UUID and world, or an empty Optional if not found
     */
    default CompletableFuture<Bank> tryGetBank(final UUID uuid, @Nullable final World world) {
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
    default CompletableFuture<Boolean> deleteBank(final Bank bank) {
        return deleteBank(bank.getOwner(), bank.getWorld().orElse(null));
    }

    /**
     * Deletes a bank associated with the specified player.
     *
     * @param player the player whose bank is to be deleted
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    default CompletableFuture<Boolean> deleteBank(final OfflinePlayer player) {
        return deleteBank(player, null);
    }

    /**
     * Deletes a bank associated with the specified player in the given world.
     *
     * @param player the player whose bank is to be deleted
     * @param world  the world where the bank is located
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    default CompletableFuture<Boolean> deleteBank(final OfflinePlayer player, @Nullable final World world) {
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
    default CompletableFuture<Boolean> deleteBank(final UUID uuid) {
        return deleteBank(uuid, null);
    }

    /**
     * Deletes a bank with the specified UUID in the given world.
     *
     * @param uuid  the UUID of the bank to be deleted
     * @param world the world where the bank is located
     * @return a CompletableFuture that completes with a boolean value indicating whether the deletion was successful
     */
    CompletableFuture<Boolean> deleteBank(UUID uuid, @Nullable World world);

    /**
     * Retrieves a set of all banks.
     *
     * @return a set of all banks
     */
    @Unmodifiable
    default Set<Bank> getBanks() {
        return getBanks(null);
    }

    /**
     * Retrieves a set of all banks in the {@link World}.
     *
     * @param world the world from which to retrieve the banks
     * @return a {@code Set<Bank>} containing all the banks in the world
     */
    @Unmodifiable
    Set<Bank> getBanks(@Nullable World world);

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
    default Optional<Bank> getBank(final OfflinePlayer player) {
        return getBank(player, null);
    }

    /**
     * Retrieves the {@link Bank} associated with the specified {@link OfflinePlayer} and {@link World}.
     *
     * @param player the player whose bank is being retrieved
     * @param world  the world the bank belongs to
     * @return an {@code Optional<Bank>} containing the bank associated with the player and world, or empty if not found
     */
    default Optional<Bank> getBank(final OfflinePlayer player, @Nullable final World world) {
        return getBank(player.getUniqueId(), world);
    }

    /**
     * Retrieves the {@link Bank} associated with the specified UUID.
     *
     * @param uuid the UUID of the bank's owner
     * @return an Optional containing the Bank associated with the UUID, or empty if not found
     */
    default Optional<Bank> getBank(final UUID uuid) {
        return getBank(uuid, null);
    }

    /**
     * Retrieves the {@link Bank} associated with the specified UUID and world.
     *
     * @param uuid  the UUID of the bank's owner
     * @param world the world the bank belongs to
     * @return an Optional containing the Bank associated with the UUID and world, or empty if not found
     */
    Optional<Bank> getBank(UUID uuid, @Nullable World world);

    /**
     * Determines whether the controller supports handling of multiple worlds.
     *
     * @return {@code true} if multi-world banking is supported, otherwise {@code false}
     * @implSpec If multiple worlds are not supported,
     * implementations must ignore world-specific parameters and only handle cases where the world parameter is null.
     * @since 3.0.0
     */
    @Contract(pure = true)
    boolean hasMultiWorldSupport();
}
