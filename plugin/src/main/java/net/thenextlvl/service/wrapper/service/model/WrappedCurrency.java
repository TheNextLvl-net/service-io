package net.thenextlvl.service.wrapper.service.model;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

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
    public Optional<Component> getDisplayNameSingular(Locale locale) {
        return Optional.ofNullable(economy.currencyNameSingular()).map(Component::text);
    }

    @Override
    public Optional<Component> getDisplayNamePlural(Locale locale) {
        return Optional.ofNullable(economy.currencyNamePlural()).map(Component::text);
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

    @Override
    public boolean editCurrency(Consumer<Builder> consumer) {
        return false;
    }
}
