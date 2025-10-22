package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedAccount;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public class EconomyServiceWrapper implements EconomyController {
    private final Economy economy;
    private final Plugin provider;

    public EconomyServiceWrapper(Economy economy, Plugin provider) {
        this.economy = economy;
        this.provider = provider;
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
    public CompletableFuture<@Unmodifiable Set<Account>> loadAccounts() {
        return CompletableFuture.completedFuture(getAccounts());
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(economy::hasAccount)
                .map(player -> new WrappedAccount(null, economy, player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Account> getAccount(OfflinePlayer player) {
        if (!economy.hasAccount(player)) return Optional.empty();
        return Optional.of(new WrappedAccount(null, economy, player));
    }

    @Override
    public Optional<Account> getAccount(OfflinePlayer player, World world) {
        if (!economy.hasAccount(player, world.getName())) return Optional.empty();
        return Optional.of(new WrappedAccount(world, economy, player));
    }

    @Override
    public Optional<Account> getAccount(UUID uuid) {
        return getAccount(provider.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public Optional<Account> getAccount(UUID uuid, World world) {
        return getAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Account> createAccount(OfflinePlayer player) {
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player))
                .thenApply(account -> getAccount(player).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(OfflinePlayer player, World world) {
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player, world.getName()))
                .thenApply(account -> getAccount(player, world).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(UUID uuid) {
        return createAccount(provider.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<Account> createAccount(UUID uuid, World world) {
        return createAccount(provider.getServer().getOfflinePlayer(uuid), world);
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
        return loadAccount(provider.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(UUID uuid, World world) {
        return loadAccount(provider.getServer().getOfflinePlayer(uuid), world);
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
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return economy.getName();
    }
}
