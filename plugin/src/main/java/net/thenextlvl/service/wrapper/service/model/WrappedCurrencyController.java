package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import net.thenextlvl.service.api.economy.currency.CurrencyData;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
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
    public Currency createCurrency(final CurrencyData data) throws IllegalArgumentException {
        throw new CapabilityException(plugin, EconomyCapability.MULTI_CURRENCY);
    }

    @Override
    public boolean deleteCurrency(final String name) {
        return false;
    }
}
