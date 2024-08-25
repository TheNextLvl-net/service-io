package net.thenextlvl.service.api.economy.bank;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BankController {
    default CompletableFuture<Bank> createBank(OfflinePlayer player, String name) throws IllegalStateException {
        return createBank(player.getUniqueId(), name);
    }

    default CompletableFuture<Bank> createBank(OfflinePlayer player, String name, World world) throws IllegalStateException {
        return createBank(player.getUniqueId(), name, world);
    }

    CompletableFuture<Bank> createBank(UUID uuid, String name) throws IllegalStateException;

    CompletableFuture<Bank> createBank(UUID uuid, String name, World world) throws IllegalStateException;

    default CompletableFuture<Bank> loadBank(OfflinePlayer player) {
        return loadBank(player.getUniqueId());
    }

    default CompletableFuture<Bank> loadBank(OfflinePlayer player, World world) {
        return loadBank(player.getUniqueId(), world);
    }

    CompletableFuture<Bank> loadBank(UUID uuid);

    CompletableFuture<Bank> loadBank(UUID uuid, World world);

    CompletableFuture<Set<Bank>> loadBanks();

    CompletableFuture<Set<Bank>> loadBanks(String name);

    CompletableFuture<Set<Bank>> loadBanks(String name, World world);

    default CompletableFuture<Bank> tryGetBank(OfflinePlayer player) {
        return getBank(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(player));
    }

    default CompletableFuture<Bank> tryGetBank(OfflinePlayer player, World world) {
        return getBank(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(player, world));
    }

    default CompletableFuture<Bank> tryGetBank(UUID uuid) {
        return getBank(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(uuid));
    }

    default CompletableFuture<Bank> tryGetBank(UUID uuid, World world) {
        return getBank(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadBank(uuid, world));
    }

    default CompletableFuture<Boolean> deleteBank(Bank bank) {
        return bank.getWorld()
                .map(world -> deleteBank(bank.getOwner(), world))
                .orElseGet(() -> deleteBank(bank.getOwner()));
    }

    default CompletableFuture<Boolean> deleteBank(OfflinePlayer player) {
        return deleteBank(player.getUniqueId());
    }

    default CompletableFuture<Boolean> deleteBank(OfflinePlayer player, World world) {
        return deleteBank(player.getUniqueId(), world);
    }

    CompletableFuture<Boolean> deleteBank(UUID uuid);

    CompletableFuture<Boolean> deleteBank(UUID uuid, World world);

    List<Bank> getBanks();

    default Optional<Bank> getBank(OfflinePlayer player) {
        return getBank(player.getUniqueId());
    }

    default Optional<Bank> getBank(OfflinePlayer player, World world) {
        return getBank(player.getUniqueId(), world);
    }

    Optional<Bank> getBank(UUID uuid);

    Optional<Bank> getBank(UUID uuid, World world);

    /**
     * Retrieves the name associated with the bank controller.
     *
     * @return the name of the bank controller.
     */
    @Contract(pure = true)
    String getName();
}
