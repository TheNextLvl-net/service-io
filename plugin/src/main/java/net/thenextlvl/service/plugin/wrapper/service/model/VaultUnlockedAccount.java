package net.thenextlvl.service.plugin.wrapper.service.model;

import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.economy.Account;
import net.thenextlvl.service.economy.TransactionResult;
import net.thenextlvl.service.economy.currency.Currency;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public final class VaultUnlockedAccount implements Account {
    private final @Nullable World world;
    private final Economy economy;
    private final UUID owner;
    private final String pluginName;

    public VaultUnlockedAccount(@Nullable final World world, final Economy economy, final UUID owner, final String pluginName) {
        this.world = world;
        this.economy = economy;
        this.owner = owner;
        this.pluginName = pluginName;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
    }

    @Override
    public BigDecimal getBalance(final Currency currency) {
        if (!canHold(currency)) throw new IllegalArgumentException("Currency not supported: " + currency);
        if (world != null) return economy.balance(pluginName, owner, world.getName());
        return economy.balance(pluginName, owner);
    }

    @Override
    public TransactionResult deposit(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var bdAmount = new BigDecimal(amount.toString());
        final var response = world != null
                ? economy.deposit(pluginName, owner, world.getName(), bdAmount)
                : economy.deposit(pluginName, owner, bdAmount);
        return new TransactionResult(currency, amount, response.balance, switch (response.type) {
            case SUCCESS -> TransactionResult.Status.SUCCESS;
            case FAILURE, NOT_IMPLEMENTED -> TransactionResult.Status.FAILURE;
        });
    }

    @Override
    public TransactionResult withdraw(final Number amount, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var bdAmount = new BigDecimal(amount.toString());
        final var response = world != null
                ? economy.withdraw(pluginName, owner, world.getName(), bdAmount)
                : economy.withdraw(pluginName, owner, bdAmount);
        return new TransactionResult(currency, amount, response.balance, switch (response.type) {
            case SUCCESS -> TransactionResult.Status.SUCCESS;
            case FAILURE -> response.amount.compareTo(response.balance) > 0
                    ? TransactionResult.Status.INSUFFICIENT_FUNDS
                    : TransactionResult.Status.FAILURE;
            case NOT_IMPLEMENTED -> TransactionResult.Status.FAILURE;
        });
    }

    @Override
    public TransactionResult setBalance(final Number balance, final Currency currency) {
        if (!canHold(currency)) return TransactionResult.unsupported(currency);
        final var current = getBalance(currency);
        final var difference = new BigDecimal(balance.toString()).subtract(current);
        if (difference.compareTo(BigDecimal.ZERO) > 0) return deposit(difference, currency);
        else if (difference.compareTo(BigDecimal.ZERO) < 0) return withdraw(difference.negate(), currency);
        return new TransactionResult(currency, balance, current, TransactionResult.Status.SUCCESS);
    }

    @Override
    public boolean canHold(final Currency currency) {
        return currency instanceof VaultUnlockedCurrency(
                final Economy wrapped, final String name
        ) && wrapped == economy;
    }
}
