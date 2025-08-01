package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.wrapper.service.model.WrappedBank;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

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

    public BankServiceWrapper(Economy economy, Plugin provider, ServicePlugin plugin) {
        this.economy = economy;
        this.plugin = plugin;
        this.provider = provider;
    }

    @Override
    public String format(Number amount) {
        return economy.format(amount.doubleValue());
    }

    @Override
    public int fractionalDigits() {
        return economy.fractionalDigits();
    }

    @Override
    public CompletableFuture<Bank> createBank(OfflinePlayer player, String name) {
        return CompletableFuture.completedFuture(economy.createBank(name, player))
                .thenApply(bank -> getBank(name).orElseThrow());
    }

    @Override
    public CompletableFuture<Bank> createBank(OfflinePlayer player, String name, World world) {
        return createBank(player, name);
    }

    @Override
    public CompletableFuture<Bank> createBank(UUID uuid, String name) {
        return createBank(plugin.getServer().getOfflinePlayer(uuid), name);
    }

    @Override
    public CompletableFuture<Bank> createBank(UUID uuid, String name, World world) {
        return createBank(plugin.getServer().getOfflinePlayer(uuid), name, world);
    }

    @Override
    public CompletableFuture<Bank> loadBank(String name) {
        return CompletableFuture.completedFuture(getBank(name).orElse(null));
    }

    @Override
    public CompletableFuture<Bank> loadBank(UUID uuid) {
        return CompletableFuture.completedFuture(getBank(uuid).orElse(null));
    }

    @Override
    public CompletableFuture<Bank> loadBank(UUID uuid, World world) {
        return CompletableFuture.completedFuture(getBank(uuid, world).orElse(null));
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks() {
        return CompletableFuture.completedFuture(getBanks());
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Bank>> loadBanks(World world) {
        return CompletableFuture.completedFuture(getBanks(world));
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(String name) {
        return CompletableFuture.completedFuture(economy.deleteBank(name))
                .thenApply(EconomyResponse::transactionSuccess);
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(UUID uuid) {
        return deleteBank(getBank(uuid).orElseThrow().getName());
    }

    @Override
    public CompletableFuture<Boolean> deleteBank(UUID uuid, World world) {
        return deleteBank(getBank(uuid, world).orElseThrow().getName());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks() {
        return economy.getBanks().stream()
                .map(bank -> new WrappedBank(bank, null, economy, plugin))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Bank> getBanks(World world) {
        return getBanks();
    }

    @Override
    public Optional<Bank> getBank(String name) {
        return Optional.of(new WrappedBank(name, null, economy, plugin));
    }

    @Override
    public Optional<Bank> getBank(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Bank> getBank(UUID uuid, World world) {
        return Optional.empty();
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
