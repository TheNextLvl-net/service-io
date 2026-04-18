package net.thenextlvl.service.plugin.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.economy.EconomyCapability;
import net.thenextlvl.service.economy.bank.Bank;
import net.thenextlvl.service.economy.bank.BankController;
import net.thenextlvl.service.economy.currency.CurrencyController;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import net.thenextlvl.service.plugin.wrapper.service.model.WrappedBank;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class BankServiceWrapper implements BankController, Wrapper {
    private final EnumSet<EconomyCapability> capabilities = EnumSet.of(EconomyCapability.MULTI_WORLD);
    private final EconomyServiceWrapper wrapper;
    private final Economy economy;
    private final Plugin provider;

    public BankServiceWrapper(final Economy economy, final Plugin provider, final EconomyServiceWrapper wrapper) {
        this.economy = economy;
        this.provider = provider;
        this.wrapper = wrapper;
    }

    @Override
    public CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name) {
        return CompletableFuture.completedFuture(economy.createBank(name, player))
                .thenApply(bank -> getBank(name).orElseThrow());
    }

    @Override
    public CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name, final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final String name) {
        return CompletableFuture.completedFuture(economy.deleteBank(name))
                .thenApply(EconomyResponse::transactionSuccess);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final OfflinePlayer player, final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CurrencyController getCurrencyController() {
        return wrapper.getCurrencyController();
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks() {
        return economy.getBanks().stream()
                .map(bank -> new WrappedBank(bank, economy, provider))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks(@Nullable final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
    }

    @Override
    public Optional<Bank> getBank(final String name) {
        return Optional.of(new WrappedBank(name, economy, provider));
    }

    @Override
    public Optional<Bank> getBank(final OfflinePlayer player) {
        return Optional.empty();
    }

    @Override
    public Optional<Bank> getBank(final OfflinePlayer player, @Nullable final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks() {
        return CompletableFuture.completedFuture(getBanks());
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<Optional<Bank>> loadBank(final String name) {
        return CompletableFuture.completedFuture(getBank(name));
    }

    @Override
    public CompletableFuture<Optional<Bank>> loadBank(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(getBank(player));
    }

    @Override
    public CompletableFuture<Optional<Bank>> loadBank(final OfflinePlayer player, final World world) {
        throw new CapabilityException(provider, EconomyCapability.MULTI_WORLD);
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
