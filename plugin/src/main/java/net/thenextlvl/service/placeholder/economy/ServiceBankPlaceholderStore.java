package net.thenextlvl.service.placeholder.economy;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@NullMarked
public class ServiceBankPlaceholderStore extends PlaceholderStore<BankController> {
    public ServiceBankPlaceholderStore(ServicePlugin plugin) {
        super(plugin, BankController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_bank%
        registerResolver("bank", (provider, player, matcher) -> {
            return provider.getBank(player).map(Bank::getName).orElse("");
        });

        // %serviceio_bank_balance%
        registerResolver("bank_balance", (provider, player, matcher) -> {
            return provider.getBank(player).map(Bank::getBalance).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_bank_balance_formatted%
        registerResolver("bank_balance_formatted", (provider, player, matcher) -> {
            return provider.format(provider.getBank(player).map(Bank::getBalance).orElse(BigDecimal.ZERO));
        });

        // %serviceio_bank_<world>%
        registerResolver("bank_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBank(player, world).map(Bank::getName).orElse("");
        });

        // %serviceio_bank_<world>_balance%
        registerResolver("bank_%s_balance", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBank(player, world).map(Bank::getBalance).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_bank_<world>_balance_formatted%
        registerResolver("bank_%s_balance_formatted", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.format(provider.getBank(player, world).map(Bank::getBalance).orElse(BigDecimal.ZERO));
        });

        // %serviceio_banks%
        registerResolver("banks", (provider, player, matcher) -> {
            return provider.getBanks().stream().map(Bank::getName).collect(Collectors.joining(", "));
        });

        // %serviceio_banks_<world>%
        registerResolver("banks_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBanks(world).stream().map(Bank::getName).collect(Collectors.joining(", "));
        });
    }
}
