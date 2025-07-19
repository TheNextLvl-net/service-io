package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.wrapper.service.model.WrappedBank;
import net.thenextlvl.service.wrapper.service.model.WrappedCurrency;
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
public class BankServiceWrapper implements BankController {
    private final Economy economy;
    private final Plugin provider;
    private final ServicePlugin plugin;
    private final Currency currency;

    public BankServiceWrapper(Economy economy, Plugin provider, ServicePlugin plugin) {
        this.currency = new WrappedCurrency(economy);
        this.economy = economy;
        this.plugin = plugin;
        this.provider = provider;
    }

    @Override
    public Currency getDefaultCurrency() {
        return currency;
    }

    @Override
    public CompletableFuture<Bank> createBank(OfflinePlayer player, String name, @Nullable World world) {
        return CompletableFuture.completedFuture(economy.createBank(name, player))
                .thenApply(bank -> getBank(name).orElseThrow());
    }

    @Override
    public CompletableFuture<Bank> createBank(UUID uuid, String name, @Nullable World world) {
        return createBank(plugin.getServer().getOfflinePlayer(uuid), name, world);
    }

    @Override
    public CompletableFuture<Optional<Bank>> loadBank(String name) {
        return CompletableFuture.completedFuture(getBank(name));
    }

    @Override
    public CompletableFuture<Optional<Bank>> loadBank(UUID uuid, @Nullable World world) {
        return CompletableFuture.completedFuture(getBank(uuid, world));
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(@Nullable World world) {
        return CompletableFuture.completedFuture(getBanks(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(String name) {
        return CompletableFuture.completedFuture(economy.deleteBank(name))
                .thenApply(EconomyResponse::transactionSuccess);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(UUID uuid, @Nullable World world) {
        return deleteBank(getBank(uuid, world).orElseThrow().getName());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks(@Nullable World world) {
        return world != null ? Set.of() : economy.getBanks().stream()
                .map(bank -> new WrappedBank(this, bank, null, economy, plugin))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Bank> getBank(String name) {
        return Optional.of(new WrappedBank(this, name, null, economy, plugin));
    }

    @Override
    public Optional<Bank> getBank(UUID uuid, @Nullable World world) {
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
        return economy.getName();
    }
}
