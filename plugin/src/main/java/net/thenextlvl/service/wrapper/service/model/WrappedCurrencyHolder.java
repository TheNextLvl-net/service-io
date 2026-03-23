package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WrappedCurrencyHolder implements CurrencyHolder {
    private final Currency defaultCurrency;

    public WrappedCurrencyHolder(final Economy economy) {
        this.defaultCurrency = new WrappedCurrency(this, economy);
    }

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }
}
