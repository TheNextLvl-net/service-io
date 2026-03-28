package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.TransactionResult;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public final class WrappedBank implements Bank {
    private static final UUID NIL = new UUID(0, 0);

    private final Economy economy;
    private final Plugin provider;
    private final String name;

    public WrappedBank(final String name, final Economy economy, final Plugin provider) {
        this.economy = economy;
        this.name = name;
        this.provider = provider;
    }

    @Override
    public UUID getOwner() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankOwner(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .findAny()
                .orElse(NIL);
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.empty();
    }

    @Override
    public BigDecimal getBalance(final Currency currency) {
        if (!canHold(currency)) throw new IllegalArgumentException("Currency not supported: " + currency);
        return new BigDecimal(economy.bankBalance(name).balance);
    }

    @Override
    public TransactionResult deposit(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var response = economy.bankDeposit(name, amount.doubleValue());
        return new TransactionResult(currency, amount, response.balance, switch (response.type) {
            case SUCCESS -> TransactionResult.Status.SUCCESS;
            case FAILURE, NOT_IMPLEMENTED -> TransactionResult.Status.FAILURE;
        });
    }

    @Override
    public TransactionResult withdraw(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var response = economy.bankWithdraw(name, amount.doubleValue());
        return new TransactionResult(currency, amount, response.balance, switch (response.type) {
            case SUCCESS -> TransactionResult.Status.SUCCESS;
            case FAILURE -> response.amount > response.balance
                    ? TransactionResult.Status.INSUFFICIENT_FUNDS
                    : TransactionResult.Status.FAILURE;
            case NOT_IMPLEMENTED -> TransactionResult.Status.FAILURE;
        });
    }

    @Override
    public TransactionResult setBalance(final Number balance, final Currency currency) {
        final var current = getBalance(currency);
        final var difference = balance.doubleValue() - current.doubleValue();
        if (difference > 0) return deposit(difference, currency);
        else if (difference < 0) return withdraw(-difference, currency);
        return new TransactionResult(currency, balance, current, TransactionResult.Status.SUCCESS);
    }

    @Override
    public boolean canHold(final Currency currency) {
        return currency instanceof WrappedCurrency(final Economy wrapped) && wrapped == economy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Unmodifiable Set<UUID> getMembers() {
        return Arrays.stream(provider.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankMember(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());
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
