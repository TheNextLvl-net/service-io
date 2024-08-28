package net.thenextlvl.service.wrapper.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WrappedBank implements Bank {
    private final @Getter String name;
    private final @Nullable World world;
    private final Economy economy;
    private final ServicePlugin plugin;

    @Override
    public BigDecimal deposit(Number amount) {
        return new BigDecimal(economy.bankDeposit(name, amount.doubleValue()).balance);
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(economy.bankBalance(name).balance);
    }

    @Override
    public BigDecimal withdraw(Number amount) {
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
    public void setBalance(Number balance) {
        var difference = balance.doubleValue() - getBalance().doubleValue();
        if (difference > 0) deposit(difference);
        else if (difference < 0) withdraw(-difference);
    }

    @Override
    public @Unmodifiable Set<UUID> getMembers() {
        return Arrays.stream(plugin.getServer().getOfflinePlayers())
                .filter(player -> economy.isBankMember(name, player).transactionSuccess())
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toUnmodifiableSet());
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
