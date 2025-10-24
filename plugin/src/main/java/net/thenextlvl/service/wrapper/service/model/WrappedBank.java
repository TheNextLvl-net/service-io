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
public class WrappedBank implements Bank {
    private final @Nullable World world;
    private final Economy economy;
    private final Plugin provider;
    private final String name;

    public WrappedBank(String name, @Nullable World world, Economy economy, Plugin provider) {
        this.name = name;
        this.world = world;
        this.economy = economy;
        this.provider = provider;
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(economy.bankBalance(name).balance);
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
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
    public BigDecimal setBalance(Number balance) {
        var difference = balance.doubleValue() - getBalance().doubleValue();
        if (difference > 0) {
            return new BigDecimal(economy.bankDeposit(name, difference).balance);
        } else if (difference < 0) {
            return new BigDecimal(economy.bankWithdraw(name, -difference).balance);
        }
        return BigDecimal.ZERO;
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

    @Override
    public boolean canDeposit(OfflinePlayer player, Number amount, Currency currency) {
        return economy.isBankOwner(getName(), player).transactionSuccess();
    }

    @Override
    public boolean canDeposit(UUID uuid, Number amount, Currency currency) {
        return canDeposit(provider.getServer().getOfflinePlayer(uuid), amount, currency);
    }

    @Override
    public boolean canWithdraw(OfflinePlayer player, Number amount, Currency currency) {
        return economy.isBankOwner(getName(), player).transactionSuccess();
    }

    @Override
    public boolean canWithdraw(UUID uuid, Number amount, Currency currency) {
        return canWithdraw(provider.getServer().getOfflinePlayer(uuid), amount, currency);
    }
}
