package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import net.thenextlvl.service.api.economy.currency.CurrencyData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WrappedCurrencyController implements CurrencyController {
    private final Currency defaultCurrency;

    public WrappedCurrencyController(final Economy economy) {
        this.defaultCurrency = new WrappedCurrency(economy);
    }

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public Currency createCurrency(final CurrencyData data) throws IllegalArgumentException {
        throw new CapabilityException(EconomyCapability.MULTI_CURRENCY);
    }

    @Override
    public boolean deleteCurrency(final String name) {
        return false;
    }
}
