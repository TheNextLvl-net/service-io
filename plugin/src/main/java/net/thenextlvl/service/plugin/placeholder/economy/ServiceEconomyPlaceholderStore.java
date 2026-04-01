package net.thenextlvl.service.plugin.placeholder.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.plugin.placeholder.api.PlaceholderStore;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Locale;

public final class ServiceEconomyPlaceholderStore extends PlaceholderStore<EconomyController> {
    public ServiceEconomyPlaceholderStore(final Plugin plugin) {
        super(plugin, EconomyController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_balance%
        registerResolver("balance", (provider, player, matcher) -> {
            final var currency = provider.getCurrencyController().getDefaultCurrency();
            return provider.getAccount(player).map(account -> account.getBalance(currency)).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_balance_<world>%
        registerResolver("balance_%s", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;

            final var currency = provider.getCurrencyController().getDefaultCurrency();
            return provider.getAccount(player, world).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO).toPlainString();
        });

        // %serviceio_balance_formatted%
        registerResolver("balance_formatted", (provider, player, matcher) -> {
            final var currency = provider.getCurrencyController().getDefaultCurrency();
            final var balance = provider.getAccount(player).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO);

            final var online = player.getPlayer();
            final var locale = online != null ? online.locale() : Locale.US;

            final var format = currency.format(balance, locale);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_balance_formatted_<world>%
        registerResolver("balance_formatted_%s", (provider, player, matcher) -> {
            final var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;

            final var currency = provider.getCurrencyController().getDefaultCurrency();
            final var balance = provider.getAccount(player, world).map(bank -> bank.getBalance(currency)).orElse(BigDecimal.ZERO);

            final var online = player.getPlayer();
            final var locale = online != null ? online.locale() : Locale.US;

            final var format = currency.format(balance, locale);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });
    }
}
