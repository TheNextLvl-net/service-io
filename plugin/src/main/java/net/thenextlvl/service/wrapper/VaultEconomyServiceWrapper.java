package net.thenextlvl.service.wrapper;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.ParametersAreNullableByDefault;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@FieldsAreNotNullByDefault
@ParametersAreNullableByDefault
public class VaultEconomyServiceWrapper implements Economy {
    private final @Nullable BankController bankController;
    private final EconomyController economyController;
    private final ServicePlugin plugin;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return economyController.getName();
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
    public boolean hasAccount(String name, String worldName) {
        return getAccount(name, worldName).isPresent();
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
    public double getBalance(String name, String worldName) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
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
    public EconomyResponse withdrawPlayer(String name, String worldName, double amount) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return withdrawPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return getAccount(player, null).map(account -> {
            var balance = account.getBalance();
            var withdraw = account.withdraw(amount);
            var responseType = balance.equals(withdraw)
                    ? EconomyResponse.ResponseType.SUCCESS
                    : EconomyResponse.ResponseType.FAILURE;
            return new EconomyResponse(amount, withdraw.doubleValue(), responseType, null);
        }).orElse(null);
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
        return getAccount(player, null).map(account -> {
            var balance = account.getBalance();
            var deposit = account.deposit(amount);
            var responseType = balance.equals(deposit)
                    ? EconomyResponse.ResponseType.SUCCESS
                    : EconomyResponse.ResponseType.FAILURE;
            return new EconomyResponse(amount, deposit.doubleValue(), responseType, null);
        }).orElse(null);
    }

    @Override
    public EconomyResponse createBank(String name, String playerName) {
        var player = playerName != null ? plugin.getServer().getOfflinePlayerIfCached(playerName) : null;
        return createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return isBankOwner(name, player);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return isBankMember(name, player);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return Optional.ofNullable(bankController).map(BankController::getBanks)
                .map(future -> future.join().stream().map(Bank::getName).toList())
                .orElse(List.of());
    }

    @Override
    public boolean createPlayerAccount(String name) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player, null);
    }

    @Override
    public boolean createPlayerAccount(String name, String worldName) {
        var player = name != null ? plugin.getServer().getOfflinePlayerIfCached(name) : null;
        return createPlayerAccount(player, worldName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return Optional.ofNullable(player).flatMap(online -> Optional.ofNullable(worldName)
                        .map(plugin.getServer()::getWorld)
                        .map(world -> economyController.createAccount(player, world).join()))
                .isPresent();
    }

    private Optional<Account> getAccount(String name, String worldName) {
        return Optional.ofNullable(name)
                .map(plugin.getServer()::getOfflinePlayerIfCached)
                .flatMap(player -> getAccount(player, worldName));
    }

    private Optional<Account> getAccount(OfflinePlayer player, String worldName) {
        return Optional.ofNullable(player).flatMap(online -> Optional.ofNullable(worldName)
                .map(plugin.getServer()::getWorld)
                .map(world -> economyController.getAccount(online, world).join()));
    }
}
