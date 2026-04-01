package net.thenextlvl.service.economy.currency;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.Optional;

/**
 * Represents a read-only view of a currency with support for localization, formatting,
 * and symbolic representation.
 *
 * @since 3.0.0
 */
public interface Currency {
    /**
     * Retrieves the unique identifier of the currency.
     *
     * @return the name of the currency as a string
     */
    @Contract(pure = true)
    String getName();

    /**
     * Retrieves the currency symbol.
     *
     * @return the currency symbol as a component
     */
    @Contract(pure = true)
    Component getSymbol();

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits
     */
    @Contract(pure = true)
    int getFractionalDigits();

    /**
     * Retrieves the singular display name component of the currency based on the audience's locale.
     * <p>
     * If the audience does not specify a locale, {@link Locale#US} is used.
     *
     * @param audience the audience whose locale is used to determine the singular display name
     * @return an {@code Optional} containing the singular display name, or empty
     */
    default Optional<Component> getDisplayNameSingular(final Audience audience) {
        return getDisplayNameSingular(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the singular display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the singular display name should be retrieved
     * @return an {@code Optional} containing the singular display name, or empty
     */
    Optional<Component> getDisplayNameSingular(Locale locale);

    /**
     * Retrieves the plural display name component of the currency based on the audience's locale.
     * <p>
     * If the audience does not specify a locale, {@link Locale#US} is used.
     *
     * @param audience the audience whose locale is used to determine the plural display name
     * @return an {@code Optional} containing the plural display name, or empty
     */
    default Optional<Component> getDisplayNamePlural(final Audience audience) {
        return getDisplayNamePlural(audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Retrieves the plural display name component of the currency for the specified locale.
     *
     * @param locale the locale for which the plural display name should be retrieved
     * @return an {@code Optional} containing the plural display name, or empty
     */
    Optional<Component> getDisplayNamePlural(Locale locale);

    /**
     * Creates a {@link CurrencyData} snapshot from this currency's current state.
     *
     * @return a currency data instance reflecting this currency's properties
     */
    CurrencyData toData();

    /**
     * Formats the specified amount as a component for the given audience.
     *
     * @param amount   the amount to be formatted
     * @param audience the audience to format the amount for
     * @return the formatted amount as a component
     * @see #format(Number, Locale)
     */
    default Component format(final Number amount, final Audience audience) {
        return format(amount, audience.getOrDefault(Identity.LOCALE, Locale.US));
    }

    /**
     * Formats the specified amount as a component for the given locale.
     *
     * @param amount the amount to be formatted
     * @param locale the locale to format the amount in
     * @return the formatted amount as a component
     */
    Component format(Number amount, Locale locale);
}
