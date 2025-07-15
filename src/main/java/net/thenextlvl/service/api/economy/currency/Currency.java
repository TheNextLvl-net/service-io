package net.thenextlvl.service.api.economy.currency;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;

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
     * @return the singular display name as a {@code Component} for the audience's locale
     */
    default Component getDisplayNameSingular(Audience audience) {
        return getDisplayNameSingular(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the singular display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the singular display name should be retrieved
     * @return the singular display name as a {@code Component} for the specified locale
     */
    Component getDisplayNameSingular(Locale locale);


    /**
     * Retrieves the plural display name component of the currency based on the audience's locale.
     * <p>
     * If the audience does not specify a locale, {@link Locale#US} is used.
     *
     * @param audience the audience whose locale is used to determine the plural display name
     * @return the plural display name as a {@code Component} for the audience's locale
     */
    default Component getDisplayNamePlural(Audience audience) {
        return getDisplayNamePlural(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the plural display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the plural display name should be retrieved
     * @return the plural display name as a {@code Component} for the specified locale
     */
    Component getDisplayNamePlural(Locale locale);

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
     * A builder interface for constructing instances of {@link Currency}.
     * The {@code Builder} allows for the configuration of currency properties such as
     * singular and plural display names, currency symbol, and fractional digits.
     */
    interface Builder {
        /**
         * Sets the singular display name of the currency for a specific locale.
         *
         * @param name   the singular display name component of the currency
         * @param locale the locale for which the singular display name is being set
         * @return the builder instance for chaining
         */
        @Contract(value = "_, _ -> this", pure = true)
        Builder displayNameSingular(Component name, Locale locale);

        /**
         * Retrieves the singular display name component of the currency for the specified locale.
         *
         * @param locale the locale for which the singular display name should be retrieved
         * @return an {@code Optional} containing the singular display name as a {@code Component}, or empty
         */
        @Contract(value = "_ -> new")
        Optional<Component> displayNameSingular(Locale locale);

        /**
         * Sets the plural display name of the currency for a specific locale.
         *
         * @param name   the plural display name component of the currency
         * @param locale the locale for which the plural display name is being set
         * @return the builder instance for chaining
         */
        @Contract(value = "_, _ -> this", pure = true)
        Builder displayNamePlural(Component name, Locale locale);

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
         * @param symbol the symbol component to represent the currency
         * @return the builder instance for chaining
         */
        @Contract(value = "_ -> this", pure = true)
        Builder symbol(Component symbol);

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
         * @param fractionalDigits the number of fractional digits to set (must be a non-negative integer)
         * @return the builder instance for chaining
         * @throws IllegalArgumentException if {@code fractionalDigits} is negative
         */
        @Contract(value = "_ -> this")
        Builder fractionalDigits(int fractionalDigits) throws IllegalArgumentException;

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

        /**
         * Builds and returns a {@link Currency} instance based on the properties set on the {@code Builder}.
         *
         * @return the constructed {@link Currency} instance
         */
        @Contract("-> new")
        @ApiStatus.OverrideOnly
        Currency build();
    }
}
