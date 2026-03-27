package net.thenextlvl.service.placeholder.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Collectors;

@NullMarked
public final class ServiceBankPlaceholderStore extends PlaceholderStore<BankController> {
    public ServiceBankPlaceholderStore(final Plugin plugin) {
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
            final var currency = provider.getCurrencyController().getDefaultCurrency();
            return provider.getBank(player).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_bank_balance_formatted%
        registerResolver("bank_balance_formatted", (provider, player, matcher) -> {
            final var currency = provider.getCurrencyController().getDefaultCurrency();
            final var balance = provider.getBank(player).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO);

            final var online = player.getPlayer();
            final var locale = online != null ? online.locale() : Locale.US;

            final var format = currency.format(balance, locale);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_bank_<world>%
        registerResolver("bank_%s", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBank(player, world).map(Bank::getName).orElse("");
        });

        // %serviceio_bank_<world>_balance%
        registerResolver("bank_%s_balance", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;

            final var currency = provider.getCurrencyController().getDefaultCurrency();
            return provider.getBank(player, world).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_bank_<world>_balance_formatted%
        registerResolver("bank_%s_balance_formatted", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;

            final var currency = provider.getCurrencyController().getDefaultCurrency();
            final var balance = provider.getBank(player, world).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO);

            final var online = player.getPlayer();
            final var locale = online != null ? online.locale() : Locale.US;

            final var format = currency.format(balance, locale);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_banks%
        registerResolver("banks", (provider, player, matcher) -> {
            return provider.getBanks().stream().map(Bank::getName).collect(Collectors.joining(", "));
        });

        // %serviceio_banks_<world>%
        registerResolver("banks_%s", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getBanks(world).stream().map(Bank::getName).collect(Collectors.joining(", "));
        });
    }
}
