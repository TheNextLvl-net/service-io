package net.thenextlvl.service.plugin.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.economy.Account;
import net.thenextlvl.service.economy.EconomyCapability;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.currency.CurrencyController;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import net.thenextlvl.service.plugin.wrapper.service.model.WrappedAccount;
import net.thenextlvl.service.plugin.wrapper.service.model.WrappedCurrencyController;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class EconomyServiceWrapper implements EconomyController, Wrapper {
    private final EnumSet<EconomyCapability> capabilities = EnumSet.of(EconomyCapability.MULTI_WORLD);
    private final CurrencyController controller;
    private final Economy economy;
    private final Plugin provider;

    public EconomyServiceWrapper(final Economy economy, final Plugin provider) {
        this.controller = new WrappedCurrencyController(economy, provider);
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public CurrencyController getCurrencyController() {
        return controller;
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(economy::hasAccount)
                .map(player -> new WrappedAccount(null, economy, player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts(final World world) {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(offlinePlayer -> economy.hasAccount(offlinePlayer, world.key().asString()))
                .map(player -> new WrappedAccount(null, economy, player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Account> getAccount(final OfflinePlayer player) {
        if (!economy.hasAccount(player)) return Optional.empty();
        return Optional.of(new WrappedAccount(null, economy, player));
    }

    @Override
    public Optional<Account> getAccount(final OfflinePlayer player, @Nullable final World world) {
        if (world == null) return getAccount(player);
        if (!economy.hasAccount(player, world.getName())) return Optional.empty();
        return Optional.of(new WrappedAccount(world, economy, player));
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Account>> loadAccounts() {
        return CompletableFuture.completedFuture(getAccounts());
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Account>> loadAccounts(final World world) {
        return CompletableFuture.completedFuture(getAccounts(world));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(getAccount(player));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(final OfflinePlayer player, final World world) {
        return CompletableFuture.completedFuture(getAccount(player, world));
    }

    @Override
    public CompletableFuture<Account> createAccount(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player))
                .thenApply(account -> getAccount(player).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(final OfflinePlayer player, @Nullable final World world) {
        if (world == null) return createAccount(player);
        return CompletableFuture.completedFuture(economy.createPlayerAccount(player, world.getName()))
                .thenApply(account -> getAccount(player, world).orElseThrow());
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player, final World world) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return economy.getName() + " Wrapper";
    }

    @Override
    public @Unmodifiable Set<EconomyCapability> getCapabilities() {
        return Set.copyOf(capabilities);
    }

    @Override
    public boolean hasCapabilities(final Collection<EconomyCapability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(final EconomyCapability capability) {
        return capabilities.contains(capability);
    }
}
