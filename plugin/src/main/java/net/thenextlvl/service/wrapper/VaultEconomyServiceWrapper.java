package net.thenextlvl.service.wrapper;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
import static net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS;

public class VaultEconomyServiceWrapper implements Economy {
    private final @Nullable BankController bankController;
    private final EconomyController economyController;
    private final Plugin plugin;

    public VaultEconomyServiceWrapper(@NonNull EconomyController economyController, @NonNull Plugin plugin) {
        this.bankController = plugin.getServer().getServicesManager().load(BankController.class);
        this.economyController = economyController;
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return economyController.getName();
    }

    private BankController bankController() throws UnsupportedOperationException {
        if (bankController != null) return bankController;
        throw new UnsupportedOperationException(getName() + " has no bank support");
    }

    @Override
    public boolean hasBankSupport() {
        return bankController != null;
    }

    @Override
    public int fractionalDigits() {
        return economyController.fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return economyController.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return economyController.getCurrencyNamePlural(Locale.US);
    }

    @Override
    public String currencyNameSingular() {
        return economyController.getCurrencyNameSingular(Locale.US);
    }

    @Override
    public boolean hasAccount(String name) {
        return hasAccount(name, null);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player, null);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return hasAccount(player, worldName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return getAccount(player, worldName).isPresent();
    }

    @Override
    public double getBalance(String name) {
        return getBalance(name, null);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player, null);
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return getBalance(player, worldName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return getAccount(player, worldName)
                .map(Account::getBalance)
                .map(Number::doubleValue)
                .orElse(0.0);
    }

    @Override
    public boolean has(String name, double amount) {
        return getBalance(name) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String name, String worldName, double amount) {
        return getBalance(name, worldName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return getBalance(player, worldName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        return withdrawPlayer(name, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return withdrawPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return getAccount(player, worldName).map(account -> {
            var balance = account.getBalance();
            var withdraw = account.withdraw(amount);
            var responseType = amount != 0 && balance.equals(withdraw) ? FAILURE : SUCCESS;
            return new EconomyResponse(amount, withdraw.doubleValue(), responseType, null);
        }).orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, null));
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        return depositPlayer(name, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String worldName, double amount) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return depositPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return getAccount(player, worldName).map(account -> {
            var balance = account.getBalance();
            var deposit = account.deposit(amount);
            var responseType = amount != 0 && balance.equals(deposit) ? FAILURE : SUCCESS;
            return new EconomyResponse(amount, deposit.doubleValue(), responseType, null);
        }).orElseGet(() -> new EconomyResponse(amount, 0, FAILURE, null));
    }

    @Override
    public EconomyResponse createBank(String name, String playerName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return Optional.ofNullable(player)
                .map(offline -> bankController().createBank(offline, name).join())
                .map(bank -> new EconomyResponse(0, 0, SUCCESS, null))
                .orElseGet(() -> new EconomyResponse(0, 0, FAILURE, null));
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        var deleted = bankController().deleteBank(name).join();
        return new EconomyResponse(0, 0, deleted ? SUCCESS : FAILURE, null);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        try {
            var bank = bankController().tryGetBank(name).get();
            return new EconomyResponse(0, bank.getBalance().doubleValue(), SUCCESS, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        try {
            var bank = bankController().tryGetBank(name).get();
            var balance = bank.getBalance().doubleValue();
            var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        try {
            var bank = bankController().tryGetBank(name).get();
            var balance = bank.withdraw(amount).doubleValue();
            var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        try {
            var bank = bankController().tryGetBank(name).get();
            var balance = bank.deposit(amount).doubleValue();
            var response = balance >= amount ? SUCCESS : FAILURE;
            return new EconomyResponse(amount, balance, response, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return isBankOwner(name, player);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        try {
            var bank = bankController().tryGetBank(name).get();
            var response = player != null && bank.getOwner().equals(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance().doubleValue(), response, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return isBankMember(name, player);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        try {
            var bank = bankController().tryGetBank(name).get();
            var response = player != null && bank.isMember(player.getUniqueId()) ? SUCCESS : FAILURE;
            return new EconomyResponse(0, bank.getBalance().doubleValue(), response, null);
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, FAILURE, e.getMessage());
        }
    }

    @Override
    public List<String> getBanks() {
        return bankController().getBanks().stream()
                .map(Bank::getName)
                .toList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player, null);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createPlayerAccount(player, worldName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return Optional.ofNullable(player).map(offline -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> economyController.createAccount(offline, world).join())
                        .orElseGet(() -> economyController.createAccount(offline).join()))
                .isPresent();
    }

    private Optional<Account> getAccount(OfflinePlayer player, String worldName) {
        return Optional.ofNullable(player).flatMap(offline -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .flatMap(world -> economyController.tryGetAccount(offline, world).join())
                .or(() -> economyController.tryGetAccount(offline).join()));
    }
}
