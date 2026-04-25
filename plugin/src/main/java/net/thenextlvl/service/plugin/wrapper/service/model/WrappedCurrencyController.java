package net.thenextlvl.service.plugin.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.economy.EconomyCapability;
import net.thenextlvl.service.economy.currency.Currency;
import net.thenextlvl.service.economy.currency.CurrencyController;
import net.thenextlvl.service.economy.currency.CurrencyData;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.stream.Stream;

public final class WrappedCurrencyController implements CurrencyController {
    private final WrappedCurrency defaultCurrency;
    private final Plugin plugin;

    public WrappedCurrencyController(final Economy economy, final Plugin plugin) {
        this.defaultCurrency = new WrappedCurrency(economy);
        this.plugin = plugin;
    }

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public Stream<Currency> getCurrencies() {
        return Stream.of(defaultCurrency);
    }

    @Override
    public Optional<Currency> getCurrency(final String name) {
        return defaultCurrency.getName().equals(name) ? Optional.of(defaultCurrency) : Optional.empty();
    }

    @Override
    public boolean currencyExists(final String name) {
        return defaultCurrency.getName().equals(name);
    }

    @Override
    public Currency createCurrency(final CurrencyData data) throws IllegalArgumentException {
        throw new CapabilityException(plugin, EconomyCapability.MULTI_CURRENCY);
    }

    @Override
    public boolean deleteCurrency(final String name) {
        return false;
    }
}
