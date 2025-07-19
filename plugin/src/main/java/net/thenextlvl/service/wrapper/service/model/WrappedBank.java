package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
public class WrappedBank implements Bank {
    private final BankController controller;
    private final @Nullable World world;
    private final Economy economy;
    private final ServicePlugin plugin;
    private final String name;

    public WrappedBank(BankController controller, String name, @Nullable World world, Economy economy, ServicePlugin plugin) {
        this.controller = controller;
        this.name = name;
        this.world = world;
        this.economy = economy;
        this.plugin = plugin;
    }

    @Override
    public CurrencyHolder getHolder() {
        return controller;
    }

    @Override
    public BigDecimal deposit(Number amount, Currency currency) {
        return new BigDecimal(economy.bankDeposit(name, amount.doubleValue()).balance);
    }

    @Override
    public BigDecimal getBalance(Currency currency) {
        return new BigDecimal(economy.bankBalance(name).balance);
    }

    @Override
    public BigDecimal withdraw(Number amount, Currency currency) {
        return new BigDecimal(economy.bankWithdraw(name, amount.doubleValue()).balance);
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
    }

    @Override
    public UUID getOwner() {
        return Arrays.stream(plugin.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankOwner(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .findAny()
                .orElse(new UUID(0, 0));
    }

    @Override
    public BigDecimal setBalance(Number balance, Currency currency) {
        var current = getBalance(currency);
        var difference = balance.doubleValue() - current.doubleValue();
        if (difference > 0) return deposit(difference, currency);
        else if (difference < 0) return withdraw(-difference, currency);
        return current;
    }

    @Override
    public @Unmodifiable Set<UUID> getMembers() {
        return Arrays.stream(plugin.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankMember(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean addMember(UUID uuid) {
        return false;
    }

    @Override
    public boolean isMember(UUID uuid) {
        return false;
    }

    @Override
    public boolean removeMember(UUID uuid) {
        return false;
    }

    @Override
    public boolean setOwner(UUID uuid) {
        return false;
    }
}
