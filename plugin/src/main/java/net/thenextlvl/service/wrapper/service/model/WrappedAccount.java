package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
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
    public BigDecimal deposit(final Number amount) {
        final var response = economy.depositPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
        return new BigDecimal(response.balance);
    }

    @Override
    public BigDecimal getBalance() {
        final var balance = economy.getBalance(holder, world != null ? world.getName() : null);
        return new BigDecimal(balance);
    }

    @Override
    public BigDecimal getBalance(@Nullable final Currency currency) {
        return currency != null ? BigDecimal.ZERO : getBalance();
    }

    @Override
    public BigDecimal withdraw(final Number amount) {
        final var response = economy.withdrawPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
        return new BigDecimal(response.balance);
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
    }

    @Override
    public UUID getOwner() {
        return holder.getUniqueId();
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
}
