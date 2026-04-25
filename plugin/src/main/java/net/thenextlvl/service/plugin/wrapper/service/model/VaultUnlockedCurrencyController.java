package net.thenextlvl.service.plugin.wrapper.service.model;

import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.economy.EconomyCapability;
import net.thenextlvl.service.economy.currency.Currency;
import net.thenextlvl.service.economy.currency.CurrencyController;
import net.thenextlvl.service.economy.currency.CurrencyData;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.stream.Stream;

public final class VaultUnlockedCurrencyController implements CurrencyController {
    private final VaultUnlockedCurrency defaultCurrency;
    private final Economy economy;
    private final Plugin plugin;

    public VaultUnlockedCurrencyController(final Economy economy, final Plugin plugin) {
        this.defaultCurrency = new VaultUnlockedCurrency(economy, economy.getDefaultCurrency(plugin.getName()), plugin.getName());
        this.economy = economy;
        this.plugin = plugin;
    }

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public Stream<Currency> getCurrencies() {
        return economy.currencies().stream().map(currency -> new VaultUnlockedCurrency(economy, currency, plugin.getName()));
    }

    @Override
    public Optional<Currency> getCurrency(final String name) {
        return currencyExists(name) ? Optional.of(new VaultUnlockedCurrency(economy, name, plugin.getName())) : Optional.empty();
    }

    @Override
    public boolean currencyExists(final String name) {
        return defaultCurrency.economy().hasCurrency(name);
    }

    @Override
    public Currency createCurrency(final CurrencyData data) throws IllegalArgumentException {
        throw new CapabilityException(plugin, EconomyCapability.CURRENCY_CREATION);
    }

    @Override
    public boolean deleteCurrency(final String name) {
        return false;
    }
}
