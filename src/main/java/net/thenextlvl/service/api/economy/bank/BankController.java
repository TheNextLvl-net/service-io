package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.economy.Account;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The AccountController interface provides methods to create, retrieve and delete accounts.
 */
public interface BankController {
    CompletableFuture<Account> createBank(OfflinePlayer player);

    CompletableFuture<Account> createBank(OfflinePlayer player, World world);

    CompletableFuture<Account> createBank(UUID uniqueId);

    CompletableFuture<Account> createBank(UUID uniqueId, World world);

    CompletableFuture<Account> getAccount(OfflinePlayer player);

    CompletableFuture<Account> getAccount(OfflinePlayer player, World world);

    CompletableFuture<Account> getAccount(UUID uniqueId);

    CompletableFuture<Account> getAccount(UUID uniqueId, World world);

    CompletableFuture<Boolean> deleteAccount(Account account);

    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player);

    CompletableFuture<Boolean> deleteAccount(OfflinePlayer player, World world);

    CompletableFuture<Boolean> deleteAccount(UUID uniqueId);

    CompletableFuture<Boolean> deleteAccount(UUID uniqueId, World world);

    CompletableFuture<List<Bank>> getBanks();

    /**
     * Retrieves the name associated with the economy controller.
     *
     * @return the name of the economy controller.
     */
    String getName();
}
