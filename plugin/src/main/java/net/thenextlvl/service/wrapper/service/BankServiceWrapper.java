package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedBank;
import net.thenextlvl.service.wrapper.service.model.WrappedCurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public final class BankServiceWrapper implements BankController, Wrapper {
    private final CurrencyHolder holder;
    private final Economy economy;
    private final Plugin provider;

    public BankServiceWrapper(final Economy economy, final Plugin provider) {
        this.holder = new WrappedCurrencyHolder(economy);
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public CurrencyHolder getCurrencyHolder() {
        return holder;
    }

    @Override
    @SuppressWarnings("removal")
    public String format(final Number amount) {
        return economy.format(amount.doubleValue());
    }

    @Override
    @SuppressWarnings("removal")
    public int fractionalDigits() {
        return economy.fractionalDigits();
    }

    @Override
    public CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name, @Nullable final World world) {
        return CompletableFuture.completedFuture(economy.createBank(name, player))
                .thenApply(bank -> getBank(name).orElseThrow());
    }

    @Override
    public CompletableFuture<Bank> createBank(final UUID uuid, final String name, @Nullable final World world) {
        return createBank(provider.getServer().getOfflinePlayer(uuid), name, world);
    }

    @Override
    public CompletableFuture<Bank> loadBank(final String name) {
        return CompletableFuture.completedFuture(getBank(name).orElse(null));
    }

    @Override
    public CompletableFuture<Bank> loadBank(final UUID uuid, @Nullable final World world) {
        return CompletableFuture.completedFuture(getBank(uuid, world).orElse(null));
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(@Nullable final World world) {
        return CompletableFuture.completedFuture(getBanks(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final String name) {
        return CompletableFuture.completedFuture(economy.deleteBank(name))
                .thenApply(EconomyResponse::transactionSuccess);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final UUID uuid, @Nullable final World world) {
        return deleteBank(getBank(uuid, world).orElseThrow().getName());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks(@Nullable final World world) {
        return economy.getBanks().stream()
                .map(bank -> new WrappedBank(bank, economy, provider))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Bank> getBank(final String name) {
        return Optional.of(new WrappedBank(name, economy, provider));
    }

    @Override
    public Optional<Bank> getBank(final UUID uuid, @Nullable final World world) {
        return Optional.empty();
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
}
