package net.thenextlvl.service.placeholder.economy;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

@NullMarked
public class ServiceEconomyPlaceholderStore extends PlaceholderStore<EconomyController> {
    public ServiceEconomyPlaceholderStore(ServicePlugin plugin) {
        super(plugin, EconomyController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_balance%
        registerResolver("balance", (provider, player, matcher) -> {
            return provider.getAccount(player).map(Account::getBalance).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_balance_<world>%
        registerResolver("balance_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getAccount(player, world).map(Account::getBalance).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_balance_formatted%
        registerResolver("balance_formatted", (provider, player, matcher) -> {
            return provider.format(provider.getAccount(player).map(Account::getBalance).orElse(BigDecimal.ZERO));
        });

        registerResolver("balance_formatted_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.format(provider.getAccount(player, world).map(Account::getBalance).orElse(BigDecimal.ZERO));
        });
    }
}
