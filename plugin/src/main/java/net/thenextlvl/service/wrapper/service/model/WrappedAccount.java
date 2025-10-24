package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.Account;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class WrappedAccount implements Account {
    private final @Nullable World world;
    private final Economy economy;
    private final OfflinePlayer holder;

    public WrappedAccount(@Nullable World world, Economy economy, OfflinePlayer holder) {
        this.world = world;
        this.economy = economy;
        this.holder = holder;
    }

    @Override
    public BigDecimal getBalance() {
        var balance = economy.getBalance(holder, world != null ? world.getName() : null);
        return new BigDecimal(balance);
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
    public BigDecimal setBalance(Number balance) {
        var difference = balance.doubleValue() - getBalance().doubleValue();
        if (difference > 0) {
            var response = economy.depositPlayer(holder, world != null ? world.getName() : null, difference);
            return new BigDecimal(response.balance);
        } else if (difference < 0) {
            var response = economy.withdrawPlayer(holder, world != null ? world.getName() : null, -difference);
            return new BigDecimal(response.balance);
        }
        return BigDecimal.ZERO;
    }
}
