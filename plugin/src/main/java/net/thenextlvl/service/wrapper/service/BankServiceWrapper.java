package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedBank;
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
    private final Economy economy;
    private final Plugin provider;

    public BankServiceWrapper(final Economy economy, final Plugin provider) {
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<Bank> createBank(final UUID uuid, final String name) {
        return createBank(provider.getServer().getOfflinePlayer(uuid), name);
    }

    @Override
    public CompletableFuture<Bank> createBank(final OfflinePlayer player, final String name) {
        return CompletableFuture.completedFuture(economy.createBank(name, player))
                .thenApply(bank -> getBank(name).orElseThrow());
    }

    @Override
    public CompletableFuture<Bank> createBank(final UUID uuid, final String name, final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final String name) {
        return CompletableFuture.completedFuture(economy.deleteBank(name))
                .thenApply(EconomyResponse::transactionSuccess);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final UUID uuid) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(final UUID uuid, final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks() {
        return economy.getBanks().stream()
                .map(bank -> new WrappedBank(bank, economy, provider))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks(@Nullable final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
    }

    @Override
    public Optional<Bank> getBank(final String name) {
        return Optional.of(new WrappedBank(name, economy, provider));
    }

    @Override
    public Optional<Bank> getBank(final UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Bank> getBank(final UUID uuid, @Nullable final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> resolveBanks() {
        return CompletableFuture.completedFuture(getBanks());
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> resolveBanks(final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
    }

    @Override
    public CompletableFuture<Optional<Bank>> resolveBank(final String name) {
        return CompletableFuture.completedFuture(getBank(name));
    }

    @Override
    public CompletableFuture<Optional<Bank>> resolveBank(final UUID uuid) {
        return CompletableFuture.completedFuture(getBank(uuid));
    }

    @Override
    public CompletableFuture<Optional<Bank>> resolveBank(final UUID uuid, final World world) {
        throw new CapabilityException(EconomyCapability.MULTI_WORLD);
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
