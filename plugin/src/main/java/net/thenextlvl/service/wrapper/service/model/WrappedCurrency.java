package net.thenextlvl.service.wrapper.service.model;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

@NullMarked
public class WrappedCurrency implements Currency {
    private final CurrencyHolder holder;
    private final Economy economy;

    public WrappedCurrency(CurrencyHolder holder, Economy economy) {
        this.economy = economy;
        this.holder = holder;
    }

    @Override
    public CurrencyHolder getHolder() {
        return holder;
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

    @Override
    public Builder toBuilder() {
        return new NoOpBuilder(economy);
    }

    private record NoOpBuilder(Economy economy) implements Builder {
        @Override
        public Builder name(String name) {
            return this;
        }

        @Override
        public String name() {
            return economy.getName();
        }

        @Override
        public @Unmodifiable Map<Locale, Component> displayNamesSingular() {
            return Map.of();
        }

        @Override
        public Builder displayNameSingular(Locale locale, @Nullable Component name) {
            return this;
        }

        @Override
        public Optional<Component> displayNameSingular(Locale locale) {
            return Optional.empty();
        }

        @Override
        public @Unmodifiable Map<Locale, Component> displayNamesPlural() {
            return Map.of();
        }

        @Override
        public Builder displayNamePlural(Locale locale, @Nullable Component name) {
            return this;
        }

        @Override
        public Optional<Component> displayNamePlural(Locale locale) {
            return Optional.empty();
        }

        @Override
        public Builder symbol(@Nullable Component symbol) {
            return this;
        }

        @Override
        public Optional<Component> symbol() {
            return Optional.empty();
        }

        @Override
        public Builder fractionalDigits(@Nullable Integer fractionalDigits) throws IllegalArgumentException {
            return this;
        }

        @Override
        public OptionalInt fractionalDigits() {
            return OptionalInt.of(economy.fractionalDigits());
        }
    }
}
