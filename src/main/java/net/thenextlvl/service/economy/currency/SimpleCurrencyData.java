package net.thenextlvl.service.economy.currency;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

final class SimpleCurrencyData implements CurrencyData {
    private final Map<Locale, Component> displayNamesPlural;
    private final Map<Locale, Component> displayNamesSingular;

    private Component symbol;
    private String name;
    private int fractions;

    SimpleCurrencyData(
            final String name, final Component symbol, final int fractions,
            final Map<Locale, Component> displayNamesSingular,
            final Map<Locale, Component> displayNamesPlural
    ) {
        if (fractions < 0) throw new IllegalArgumentException("fractionalDigits must be non-negative");
        this.name = name;
        this.symbol = symbol;
        this.fractions = fractions;
        this.displayNamesSingular = displayNamesSingular;
        this.displayNamesPlural = displayNamesPlural;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Component symbol() {
        return symbol;
    }

    @Override
    public int fractionalDigits() {
        return fractions;
    }

    @Override
    public Map<Locale, Component> displayNamesSingular() {
        return Collections.unmodifiableMap(displayNamesSingular);
    }

    @Override
    public Map<Locale, Component> displayNamesPlural() {
        return Collections.unmodifiableMap(displayNamesPlural);
    }

    @Override
    public CurrencyData name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public CurrencyData symbol(final Component symbol) {
        this.symbol = symbol;
        return this;
    }

    @Override
    public CurrencyData fractionalDigits(final int fractionalDigits) {
        if (fractionalDigits < 0) throw new IllegalArgumentException("fractionalDigits must be non-negative");
        this.fractions = fractionalDigits;
        return this;
    }

    @Override
    public CurrencyData displayNameSingular(final Locale locale, @Nullable final Component name) {
        if (name == null) displayNamesSingular.remove(locale);
        else displayNamesSingular.put(locale, name);
        return this;
    }

    @Override
    public CurrencyData displayNamePlural(final Locale locale, @Nullable final Component name) {
        if (name == null) displayNamesPlural.remove(locale);
        else displayNamesPlural.put(locale, name);
        return this;
    }
}
