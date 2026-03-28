package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.TransactionResult;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public final class WrappedAccount implements Account {
    private final @Nullable World world;
    private final Economy economy;
    private final OfflinePlayer holder;

    public WrappedAccount(@Nullable final World world, final Economy economy, final OfflinePlayer holder) {
        this.world = world;
        this.economy = economy;
        this.holder = holder;
    }

    @Override
    public UUID getOwner() {
        return holder.getUniqueId();
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
    }

    @Override
    public BigDecimal getBalance(final Currency currency) {
        if (!canHold(currency)) throw new IllegalArgumentException("Currency not supported: " + currency);
        return new BigDecimal(economy.getBalance(holder, world != null ? world.getName() : null));
    }

    @Override
    public TransactionResult deposit(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var response = economy.depositPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
        return new TransactionResult(currency, amount, response.balance, switch (response.type) {
            case SUCCESS -> TransactionResult.Status.SUCCESS;
            case FAILURE, NOT_IMPLEMENTED -> TransactionResult.Status.FAILURE;
        });
    }

    @Override
    public TransactionResult withdraw(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var response = economy.withdrawPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
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
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
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
}
