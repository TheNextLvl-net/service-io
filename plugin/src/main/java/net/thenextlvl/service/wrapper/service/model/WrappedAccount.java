package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import net.thenextlvl.service.api.economy.EconomyController;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class WrappedAccount implements Account {
    private final EconomyController controller;
    private final @Nullable World world;
    private final Economy economy;
    private final OfflinePlayer holder;

    public WrappedAccount(EconomyController controller, @Nullable World world, Economy economy, OfflinePlayer holder) {
        this.controller = controller;
        this.world = world;
        this.economy = economy;
        this.holder = holder;
    }

    @Override
    public CurrencyHolder getHolder() {
        return controller;
    }

    @Override
    public BigDecimal deposit(Number amount, Currency currency) {
        var response = economy.depositPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
        return new BigDecimal(response.balance);
    }

    @Override
    public BigDecimal getBalance(Currency currency) {
        var balance = economy.getBalance(holder, world != null ? world.getName() : null);
        return new BigDecimal(balance);
    }

    @Override
    public BigDecimal withdraw(Number amount, Currency currency) {
        var response = economy.withdrawPlayer(holder, world != null ? world.getName() : null, amount.doubleValue());
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
    public BigDecimal setBalance(Number balance, Currency currency) {
        var difference = balance.doubleValue() - getBalance(currency).doubleValue();
        if (difference > 0) return deposit(difference, currency);
        else if (difference < 0) return withdraw(-difference, currency);
        return BigDecimal.ZERO;
    }
}
