package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedAccount;
import net.thenextlvl.service.wrapper.service.model.WrappedCurrency;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public class EconomyServiceWrapper implements EconomyController, Wrapper {
    private final CurrencyHolder holder;
    private final Economy economy;
    private final Plugin provider;

    public EconomyServiceWrapper(Economy economy, Plugin provider) {
        this.holder = new WrappedCurrencyHolder(economy);
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public CurrencyHolder getCurrencyHolder() {
        return holder;
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Account>> loadAccounts(@Nullable World world) {
        return CompletableFuture.completedFuture(getAccounts(world));
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts() {
        return getAccounts(null);
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts(@Nullable World world) {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.hasAccount(player, world != null ? world.getName() : null))
                .map(player -> new WrappedAccount(world, economy, player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Account> getAccount(OfflinePlayer player, @Nullable World world) {
        if (!economy.hasAccount(player, world != null ? world.getName() : null)) return Optional.empty();
        return Optional.of(new WrappedAccount(world, economy, player));
    }

    @Override
    public Optional<Account> getAccount(UUID uuid, @Nullable World world) {
        return getAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Account> createAccount(OfflinePlayer player, @Nullable World world) {
        var created = economy.createPlayerAccount(player, world != null ? world.getName() : null);
        if (created) loadAccount(player, world).thenApply(account -> account.orElseThrow(() ->
                new IllegalStateException("Could not find player account after creation")));
        return CompletableFuture.failedFuture(new IllegalStateException(
                "Similar account already exists"
        ));
    }

    @Override
    public CompletableFuture<Account> createAccount(UUID uuid, @Nullable World world) {
        return createAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(OfflinePlayer player, @Nullable World world) {
        return CompletableFuture.completedFuture(getAccount(player, world));
    }

    @Override
    public CompletableFuture<Optional<Account>> loadAccount(UUID uuid, @Nullable World world) {
        return loadAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(UUID uuid, @Nullable World world) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public boolean hasMultiWorldSupport() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return economy.getName() + " Wrapper";
    }

    private static class WrappedCurrencyHolder implements CurrencyHolder {
        private final Currency currency;

        private WrappedCurrencyHolder(Economy economy) {
            this.currency = new WrappedCurrency(this, economy);
        }

        @Override
        public Currency getDefaultCurrency() {
            return currency;
        }
    }
}
