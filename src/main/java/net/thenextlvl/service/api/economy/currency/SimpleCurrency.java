package net.thenextlvl.service.api.economy.currency;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

@NullMarked
@ApiStatus.Internal
public final class SimpleCurrency implements Currency {
    public static final SimpleCurrency INSTANCE = new SimpleCurrency();

    private SimpleCurrency() {
    }

    @Override
    public CurrencyHolder getHolder() {
        return SimpleCurrencyHolder.INSTANCE;
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public Optional<Component> getDisplayNameSingular(Locale locale) {
        return Optional.empty();
    }

    @Override
    public Optional<Component> getDisplayNamePlural(Locale locale) {
        return Optional.empty();
    }

    @Override
    public Component getSymbol() {
        return Component.empty();
    }

    @Override
    public Component format(Number amount, Locale locale) {
        return Component.text(String.format(locale, "%,.2f", amount.doubleValue()));
    }

    @Override
    public int getFractionalDigits() {
        return 2;
    }

    @Override
    public boolean editCurrency(Consumer<Currency.Builder> consumer) throws IllegalArgumentException {
        return false;
    }

    @Override
    public Builder toBuilder() {
        return new SimpleCurrency.Builder();
    }
    
    public static final class Builder implements Currency.Builder {
        @Override
        public Currency.Builder name(String name) {
            return this;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public @Unmodifiable Map<Locale, Component> displayNamesSingular() {
            return Map.of();
        }

        @Override
        public Currency.Builder displayNameSingular(Locale locale, @Nullable Component name) {
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
        public Currency.Builder displayNamePlural(Locale locale, @Nullable Component name) {
            return this;
        }

        @Override
        public Optional<Component> displayNamePlural(Locale locale) {
            return Optional.empty();
        }

        @Override
        public Currency.Builder symbol(@Nullable Component symbol) {
            return this;
        }

        @Override
        public Optional<Component> symbol() {
            return Optional.empty();
        }

        @Override
        public Currency.Builder fractionalDigits(@Nullable Integer fractionalDigits) throws IllegalArgumentException {
            return this;
        }

        @Override
        public OptionalInt fractionalDigits() {
            return OptionalInt.empty();
        }
    }
}
