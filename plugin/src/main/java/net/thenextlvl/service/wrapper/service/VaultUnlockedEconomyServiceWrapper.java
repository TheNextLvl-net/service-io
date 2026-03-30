package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.VaultUnlockedAccount;
import net.thenextlvl.service.wrapper.service.model.VaultUnlockedCurrencyController;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public final class VaultUnlockedEconomyServiceWrapper implements EconomyController, Wrapper {
    private final EnumSet<EconomyCapability> capabilities = EnumSet.of(EconomyCapability.MULTI_WORLD);
    private final CurrencyController controller;
    private final Economy economy;
    private final Plugin provider;

    public VaultUnlockedEconomyServiceWrapper(final Economy economy, final Plugin provider) {
        this.controller = new VaultUnlockedCurrencyController(economy, provider);
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
                .filter(player -> economy.hasAccount(player.getUniqueId()))
                .map(player -> new VaultUnlockedAccount(null, economy, player.getUniqueId(), provider.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @Unmodifiable Set<Account> getAccounts(final World world) {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.hasAccount(player.getUniqueId(), world.getName()))
                .map(player -> new VaultUnlockedAccount(world, economy, player.getUniqueId(), provider.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Account> getAccount(final OfflinePlayer player) {
        if (!economy.hasAccount(player.getUniqueId())) return Optional.empty();
        return Optional.of(new VaultUnlockedAccount(null, economy, player.getUniqueId(), provider.getName()));
    }

    @Override
    public Optional<Account> getAccount(final OfflinePlayer player, @Nullable final World world) {
        if (world == null) return getAccount(player);
        if (!economy.hasAccount(player.getUniqueId(), world.getName())) return Optional.empty();
        return Optional.of(new VaultUnlockedAccount(world, economy, player.getUniqueId(), provider.getName()));
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
        final var name = player.getName() != null ? player.getName() : player.getUniqueId().toString();
        return CompletableFuture.completedFuture(economy.createAccount(player.getUniqueId(), name, true))
                .thenApply(created -> getAccount(player).orElseThrow());
    }

    @Override
    public CompletableFuture<Account> createAccount(final OfflinePlayer player, @Nullable final World world) {
        if (world == null) return createAccount(player);
        final var name = player.getName() != null ? player.getName() : player.getUniqueId().toString();
        return CompletableFuture.completedFuture(economy.createAccount(player.getUniqueId(), name, world.getName(), true))
                .thenApply(created -> getAccount(player, world).orElseThrow());
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player) {
        return CompletableFuture.completedFuture(economy.deleteAccount(provider.getName(), player.getUniqueId()));
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final OfflinePlayer player, final World world) {
        return CompletableFuture.completedFuture(economy.deleteAccount(provider.getName(), player.getUniqueId()));
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
