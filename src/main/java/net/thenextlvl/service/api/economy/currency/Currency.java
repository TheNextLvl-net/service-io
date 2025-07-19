package net.thenextlvl.service.api.economy.currency;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Represents a currency with support for localization, formatting, and symbolic representation.
 *
 * @since 3.0.0
 */
@NullMarked
public interface Currency {
    /**
     * Retrieves the name of the currency.
     *
     * @return the name of the currency as a string
     */
    @Contract(pure = true)
    String getName();

    /**
     * Retrieves the singular display name component of the currency based on the audience's locale.
     * <p>
     * If the audience does not specify a locale, {@link Locale#US} is used.
     *
     * @param audience the audience whose locale is used to determine the singular display name
     * @return an {@code Optional} containing the singular display name as a {@code Component} for the audience's locale, or empty
     */
    default Optional<Component> getDisplayNameSingular(Audience audience) {
        return getDisplayNameSingular(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the singular display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the singular display name should be retrieved
     * @return an {@code Optional} containing the singular display name as a {@code Component} for the specified locale, or empty
     */
    Optional<Component> getDisplayNameSingular(Locale locale);

    /**
     * Retrieves the plural display name component of the currency based on the audience's locale.
     * <p>
     * If the audience does not specify a locale, {@link Locale#US} is used.
     *
     * @param audience the audience whose locale is used to determine the plural display name
     * @return an {@code Optional} containing the plural display name as a {@code Component} for the audience's locale, or empty
     */
    default Optional<Component> getDisplayNamePlural(Audience audience) {
        return getDisplayNamePlural(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the plural display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the plural display name should be retrieved
     * @return an {@code Optional} containing the plural display name as a {@code Component} for the specified locale, or empty
     */
    Optional<Component> getDisplayNamePlural(Locale locale);

    /**
     * Retrieves the currency symbol.
     *
     * @return the currency symbol as a component
     */
    Component getSymbol();

    /**
     * Formats the specified amount as a component.
     *
     * @param amount   the amount to be formatted
     * @param audience the audience to format the amount for
     * @return the formatted amount as a component
     * @see #format(Number, Locale)
     */
    default Component format(Number amount, Audience audience) {
        return format(amount, audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Formats the specified amount as a component.
     *
     * @param amount the amount to be formatted
     * @param locale the locale to format the amount in
     * @return the formatted amount as a component
     * @see #format(Number, Audience)
     */
    Component format(Number amount, Locale locale);

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits used for formatting currency amounts
     */
    int getFractionalDigits();

    /**
     * Modifies the current configuration of the currency using the provided builder.
     * The builder allows customization of various currency properties.
     *
     * @param consumer a {@code Consumer} that accepts a {@code Builder} instance to define customizations for the currency
     * @return {@code true} if the edit succeeded, otherwise {@code false}
     * @throws IllegalArgumentException if a currency with the same name already exists
     */
    boolean editCurrency(Consumer<Builder> consumer) throws IllegalArgumentException;

    /**
     * A builder interface for constructing instances of {@link Currency}.
     * The {@code Builder} allows for the configuration of currency properties such as
     * singular and plural display names, currency symbol, and fractional digits.
     */
    interface Builder {
        /**
         * Sets the name of the currency.
         *
         * @param name the name to be set
         * @return the builder instance for method chaining
         */
        @Contract(value = "_ -> this")
        Builder name(String name);

        /**
         * Retrieves the name currently set on the builder.
         *
         * @return the name as a string, or {@code null} if not set
         */
        String name();

        /**
         * Retrieves a map containing the singular display names of the currency for various locales.
         *
         * @return an unmodifiable map of {@code Locale} and {@code Component} objects
         * representing the singular display names of the currency.
         */
        @Unmodifiable
        Map<Locale, Component> displayNamesSingular();

        /**
         * Sets the singular display name of the currency for a specific locale.
         *
         * @param locale the locale for which the singular display name is being set, or {@code null} to remove
         * @param name   the singular display name component of the currency
         * @return the builder instance for chaining
         */
        @Contract(value = "_, _ -> this")
        Builder displayNameSingular(Locale locale, @Nullable Component name);

        /**
         * Retrieves the singular display name component of the currency for the specified locale.
         *
         * @param locale the locale for which the singular display name should be retrieved
         * @return an {@code Optional} containing the singular display name as a {@code Component}, or empty
         */
        @Contract(value = "_ -> new")
        Optional<Component> displayNameSingular(Locale locale);

        /**
         * Retrieves a map containing the plural display names of the currency for various locales.
         *
         * @return an unmodifiable map of {@code Locale} and {@code Component} objects
         * representing the plural display names of the currency.
         */
        @Unmodifiable
        Map<Locale, Component> displayNamesPlural();

        /**
         * Sets the plural display name of the currency for a specific locale.
         *
         * @param locale the locale for which the plural display name is being set, or {@code null} to remove
         * @param name   the plural display name component of the currency
         * @return the builder instance for chaining
         */
        @Contract(value = "_, _ -> this")
        Builder displayNamePlural(Locale locale, @Nullable Component name);

        /**
         * Retrieves the plural display name component of the currency for the specified locale.
         *
         * @param locale the locale for which the plural display name should be retrieved
         * @return an {@code Optional} containing the plural display name as a {@code Component}, or empty
         */
        @Contract(value = "_ -> new")
        Optional<Component> displayNamePlural(Locale locale);

        /**
         * Sets the currency symbol as a {@code Component}.
         *
         * @param symbol the symbol component to represent the currency, or {@code null} to remove
         * @return the builder instance for chaining
         */
        @Contract(value = "_ -> this")
        Builder symbol(@Nullable Component symbol);

        /**
         * Retrieves the currency symbol set on the {@code Builder}.
         *
         * @return an {@code Optional} containing the symbol as a {@code Component}, or empty
         */
        @Contract(value = "-> new")
        Optional<Component> symbol();

        /**
         * Sets the number of fractional digits to be used for the currency.
         * <p>
         * Fractional digits are generally used to specify the precision of the currency values,
         * for example, 2 fractional digits for most currencies such as USD (representing cents).
         *
         * @param fractionalDigits the number of fractional digits to set (must be a non-negative integer), or {@code null} to remove
         * @return the builder instance for chaining
         * @throws IllegalArgumentException if {@code fractionalDigits} is negative
         */
        @Contract(value = "_ -> this")
        Builder fractionalDigits(@Nullable Integer fractionalDigits) throws IllegalArgumentException;

        /**
         * Retrieves the number of fractional digits set for the currency.
         * <p>
         * Fractional digits represent the precision of the currency,
         * such as 2 for most currencies like USD (representing cents).
         *
         * @return an {@code OptionalInt} containing the number of fractional digits, or empty
         */
        @Contract(value = "-> new")
        OptionalInt fractionalDigits();
    }
}
