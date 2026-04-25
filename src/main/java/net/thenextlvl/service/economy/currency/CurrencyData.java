package net.thenextlvl.service.economy.currency;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mutable data carrier for creating a currency.
 * <p>
 * Instances are obtained via {@link #of(String, Component, int)} for new currencies
 * or {@link Currency#toData()} to derive from an existing currency.
 *
 * @since 3.0.0
 */
public sealed interface CurrencyData permits SimpleCurrencyData {
    /**
     * Creates new currency data with no localized display names.
     *
     * @param name             the unique identifier of the currency
     * @param symbol           the currency symbol
     * @param fractionalDigits the number of fractional digits
     * @return a new currency data instance
     * @throws IllegalArgumentException if fractionalDigits is negative
     * @since 3.0.0
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static CurrencyData of(final String name, final Component symbol, final int fractionalDigits) {
        return new SimpleCurrencyData(name, symbol, fractionalDigits, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    /**
     * Creates new currency data with localized display names.
     *
     * @param name                 the unique identifier of the currency
     * @param symbol               the currency symbol
     * @param fractionalDigits     the number of fractional digits
     * @param displayNamesPlural   the plural display names
     * @param displayNamesSingular the singular display names
     * @return a new currency data instance
     * @throws IllegalArgumentException if fractionalDigits is negative
     * @since 3.0.0
     */
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    static CurrencyData of(
            final String name, final Component symbol, final int fractionalDigits,
            final Map<Locale, Component> displayNamesSingular,
            final Map<Locale, Component> displayNamesPlural
    ) {
        return new SimpleCurrencyData(
                name, symbol, fractionalDigits,
                new ConcurrentHashMap<>(displayNamesSingular),
                new ConcurrentHashMap<>(displayNamesPlural)
        );
    }

    /**
     * Retrieves the unique identifier of the currency.
     *
     * @return the name of the currency
     * @since 3.0.0
     */
    @Contract(pure = true)
    String name();

    /**
     * Retrieves the currency symbol.
     *
     * @return the currency symbol as a component
     * @since 3.0.0
     */
    @Contract(pure = true)
    Component symbol();

    /**
     * Retrieves the number of fractional digits.
     *
     * @return the number of fractional digits
     * @since 3.0.0
     */
    @Contract(pure = true)
    int fractionalDigits();

    /**
     * Retrieves the localized singular display names.
     *
     * @return an unmodifiable view of the singular display names
     * @since 3.0.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Map<Locale, Component> displayNamesSingular();

    /**
     * Retrieves the localized plural display names.
     *
     * @return an unmodifiable view of the plural display names
     * @since 3.0.0
     */
    @Unmodifiable
    @Contract(pure = true)
    Map<Locale, Component> displayNamesPlural();

    /**
     * Sets the name.
     *
     * @param name the new name
     * @return this instance for chaining
     * @since 3.0.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    CurrencyData name(String name);

    /**
     * Sets the symbol.
     *
     * @param symbol the new symbol
     * @return this instance for chaining
     * @since 3.0.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    CurrencyData symbol(Component symbol);

    /**
     * Sets the fractional digits.
     *
     * @param fractionalDigits the new fractional digits
     * @return this instance for chaining
     * @throws IllegalArgumentException if fractionalDigits is negative
     * @since 3.0.0
     */
    @Contract(value = "_ -> this", mutates = "this")
    CurrencyData fractionalDigits(int fractionalDigits);

    /**
     * Sets or removes a singular display name for the specified locale.
     *
     * @param locale the locale for the display name
     * @param name   the singular display name component, or {@code null} to remove
     * @return this instance for chaining
     * @since 3.0.0
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    CurrencyData displayNameSingular(Locale locale, @Nullable Component name);

    /**
     * Sets or removes a plural display name for the specified locale.
     *
     * @param locale the locale for the display name
     * @param name   the plural display name component, or {@code null} to remove
     * @return this instance for chaining
     * @since 3.0.0
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    CurrencyData displayNamePlural(Locale locale, @Nullable Component name);
}
