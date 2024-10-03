package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.wrapper.service.model.WrappedAccount;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyServiceWrapper implements EconomyController {
    private final Economy economy;
    private final ServicePlugin plugin;

    public EconomyServiceWrapper(Economy economy, ServicePlugin plugin) {
        this.economy = economy;
        this.plugin = plugin;
    }

    @Override
    public String format(Number amount) {
        return economy.format(amount.doubleValue());
    }

    @Override
    public String getCurrencyNamePlural(Locale locale) {
        return economy.currencyNamePlural();
    }

    @Override
    public String getCurrencyNameSingular(Locale locale) {
        return economy.currencyNameSingular();
    }

    @Override
    public String getCurrencySymbol() {
        return "";
    }

    @Override
    public Optional<Account> getAccount(OfflinePlayer player) {
        return Optional.of(new WrappedAccount(null, economy, player));
    }

    @Override
    public Optional<Account> getAccount(OfflinePlayer player, World world) {
        return Optional.of(new WrappedAccount(world, economy, player));
    }

    @Override
    public Optional<Account> getAccount(UUID uuid) {
        return getAccount(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public Optional<Account> getAccount(UUID uuid, World world) {
        return getAccount(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Account> createAccount(OfflinePlayer player) throws IllegalStateException {
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player))
                .thenApply(account -> getAccount(player).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(OfflinePlayer player, World world) throws IllegalStateException {
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player, world.getName()))
                .thenApply(account -> getAccount(player, world).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(UUID uuid) throws IllegalStateException {
        return createAccount(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<Account> createAccount(UUID uuid, World world) throws IllegalStateException {
        return createAccount(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player) {
        return CompletableFuture.completedFuture(getAccount(player));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player, World world) {
        return CompletableFuture.completedFuture(getAccount(player, world));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(UUID uuid) {
        return loadAccount(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(UUID uuid, World world) {
        return loadAccount(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(UUID uuid) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(UUID uuid, World world) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public int fractionalDigits() {
        return economy.fractionalDigits();
    }

    @Override
    public String getName() {
        return economy.getName();
    }
}
