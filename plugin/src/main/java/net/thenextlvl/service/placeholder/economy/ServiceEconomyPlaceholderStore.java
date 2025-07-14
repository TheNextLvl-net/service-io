package net.thenextlvl.service.placeholder.economy;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Locale;

@NullMarked
public class ServiceEconomyPlaceholderStore extends PlaceholderStore<EconomyController> {
    public ServiceEconomyPlaceholderStore(ServicePlugin plugin) {
        super(plugin, EconomyController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_balance%
        registerResolver("balance", (provider, player, matcher) -> {
            return provider.getAccount(player)
                    .map(account -> account.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO)
                    .toPlainString();
        });

        // %serviceio_balance_<world>%
        registerResolver("balance_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            return provider.getAccount(player, world)
                    .map(account -> account.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO)
                    .toPlainString();
        });

        // %serviceio_balance_formatted%
        registerResolver("balance_formatted", (provider, player, matcher) -> {
            var format = provider.getDefaultCurrency().format(provider.getAccount(player)
                    .map(account -> account.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO), Locale.US);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });

        // %serviceio_balance_formatted_<world>%
        registerResolver("balance_formatted_%s", (provider, player, matcher) -> {
            var world = plugin.getServer().getWorld(matcher.group(1));
            if (world == null) return null;
            var format = provider.getDefaultCurrency().format(provider.getAccount(player, world)
                    .map(account -> account.getBalance(provider.getDefaultCurrency()))
                    .orElse(BigDecimal.ZERO), Locale.US);
            return PlainTextComponentSerializer.plainText().serialize(format);
        });
    }
}
