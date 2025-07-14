package net.thenextlvl.service.wrapper.service;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public class WrappedCurrency implements Currency {
    private final Economy economy;

    public WrappedCurrency(Economy economy) {
        this.economy = economy;
    }

    @Override
    public String getName() {
        return economy.getName();
    }

    @Override
    public Component getDisplayNameSingular(Locale locale) {
        return Component.text(economy.currencyNameSingular());
    }

    @Override
    public Component getDisplayNamePlural(Locale locale) {
        return Component.text(economy.currencyNamePlural());
    }

    @Override
    public Component getSymbol() {
        return Component.empty();
    }

    @Override
    public Component format(Number amount, Locale locale) {
        return Component.text(economy.format(amount.doubleValue()));
    }

    @Override
    public int getFractionalDigits() {
        return economy.fractionalDigits();
    }
}
