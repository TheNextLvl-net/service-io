package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedAccount;
import net.thenextlvl.service.wrapper.service.model.WrappedCurrencyController;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NullMarked
public final class EconomyServiceWrapper implements EconomyController, Wrapper {
    private final Set<EconomyCapability> capabilities;
    private final CurrencyController controller;
    private final Economy economy;
    private final Plugin provider;

    public EconomyServiceWrapper(final Economy economy, final Plugin provider) {
        this.capabilities = economy.hasBankSupport()
                ? EnumSet.of(EconomyCapability.MULTI_WORLD, EconomyCapability.BANK)
                : EnumSet.of(EconomyCapability.MULTI_WORLD);
        this.controller = new WrappedCurrencyController(economy);
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
    public Optional<Account> getAccount(final UUID uuid) {
        return getAccount(provider.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public Optional<Account> getAccount(final UUID uuid, final World world) {
        return getAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<@Unmodifiable Set<Account>> resolveAccounts() {
        return CompletableFuture.completedFuture(getAccounts());
    }

    @Override
    public CompletableFuture<Optional<Account>> resolveAccount(final UUID uuid) {
        return CompletableFuture.completedFuture(getAccount(uuid));
    }

    @Override
    public CompletableFuture<Optional<Account>> resolveAccount(final UUID uuid, final World world) {
        return CompletableFuture.completedFuture(getAccount(uuid, world));
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
    public CompletableFuture<Account> createAccount(final UUID uuid) {
        return createAccount(provider.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<Account> createAccount(final UUID uuid, final World world) {
        return createAccount(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final UUID uuid) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteAccount(final UUID uuid, final World world) {
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
