package net.thenextlvl.service.placeholder.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Collectors;

@NullMarked
public class ServiceBankPlaceholderStore extends PlaceholderStore<BankController> {
    public ServiceBankPlaceholderStore(ServicePlugin plugin) {
        super(plugin, BankController.class);
    }

    @Override
    protected void registerResolvers(BankController provider) {
        // %serviceio_bank%
        registerResolver("bank", (player, matcher) -> {
            return provider.getBank(player).map(Bank::getName).orElse("");
        });

        // %serviceio_bank_balance%
        registerResolver("bank_balance", (player, matcher) -> {
            return provider.getBank(player)
                    .map(bank -> bank.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_bank_balance_formatted%
        registerResolver("bank_balance_formatted", (player, matcher) -> {
            var format = provider.getDefaultCurrency().format(provider.getBank(player)
                    .map(bank -> bank.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO), Locale.US);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_bank_<world>%
        registerResolver("bank_%s", (player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBank(player, world).map(Bank::getName).orElse("");
        });

        // %serviceio_bank_<world>_balance%
        registerResolver("bank_%s_balance", (player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBank(player, world)
                    .map(bank -> bank.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO)
                    .toPlainString();
        });

        // %serviceio_bank_<world>_balance_formatted%
        registerResolver("bank_%s_balance_formatted", (player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            var format = provider.getDefaultCurrency().format(provider.getBank(player, world)
                    .map(bank -> bank.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO), Locale.US);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_banks%
        registerResolver("banks", (player, matcher) -> {
            return provider.getBanks().stream().map(Bank::getName).collect(Collectors.joining(", "));
        });

        // %serviceio_banks_<world>%
        registerResolver("banks_%s", (player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBanks(world).stream().map(Bank::getName).collect(Collectors.joining(", "));
        });
    }
}
