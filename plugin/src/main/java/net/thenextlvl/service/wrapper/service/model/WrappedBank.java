package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public final class WrappedBank implements Bank {
    private final Economy economy;
    private final Plugin provider;
    private final String name;

    public WrappedBank(final String name, final Economy economy, final Plugin provider) {
        this.name = name;
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public BigDecimal deposit(final Number amount) {
        return new BigDecimal(economy.bankDeposit(name, amount.doubleValue()).balance);
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(economy.bankBalance(name).balance);
    }

    @Override
    public BigDecimal getBalance(@Nullable final Currency currency) {
        return currency != null ? BigDecimal.ZERO : getBalance();
    }

    @Override
    public BigDecimal withdraw(final Number amount) {
        return new BigDecimal(economy.bankWithdraw(name, amount.doubleValue()).balance);
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.empty();
    }

    @Override
    public UUID getOwner() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankOwner(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .findAny()
                .orElse(new UUID(0, 0));
    }

    @Override
    public BigDecimal setBalance(final Number balance) {
        final var difference = balance.doubleValue() - getBalance().doubleValue();
        if (difference > 0) return deposit(difference);
        else if (difference < 0) return withdraw(-difference);
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal setBalance(final Number balance, @Nullable final Currency currency) {
        return currency != null ? BigDecimal.ZERO : setBalance(balance);
    }

    @Override
    public @Unmodifiable Set<UUID> getMembers() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankMember(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addMember(final UUID uuid) {
        return false;
    }

    @Override
    public boolean isMember(final UUID uuid) {
        return false;
    }

    @Override
    public boolean removeMember(final UUID uuid) {
        return false;
    }

    @Override
    public boolean setOwner(final UUID uuid) {
        return false;
    }

    @Override
    public boolean canDeposit(final OfflinePlayer player, final Number amount, final Currency currency) {
        return economy.isBankOwner(getName(), player).transactionSuccess();
    }

    @Override
    public boolean canDeposit(final UUID uuid, final Number amount, final Currency currency) {
        return canDeposit(provider.getServer().getOfflinePlayer(uuid), amount, currency);
    }

    @Override
    public boolean canWithdraw(final OfflinePlayer player, final Number amount, final Currency currency) {
        return economy.isBankOwner(getName(), player).transactionSuccess();
    }

    @Override
    public boolean canWithdraw(final UUID uuid, final Number amount, final Currency currency) {
        return canWithdraw(provider.getServer().getOfflinePlayer(uuid), amount, currency);
    }
}
