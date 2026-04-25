package net.thenextlvl.service.economy.currency;

import net.kyori.adventure.text.Component;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.economy.EconomyCapability;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * Manages currencies for an economy provider.
 * <p>
 * All providers expose at least a {@link #getDefaultCurrency() default currency}.
 * Providers that support {@link EconomyCapability#MULTI_CURRENCY MULTI_CURRENCY}
 * additionally support creating, deleting, and querying multiple currencies.
 *
 * @since 3.0.0
 */
public interface CurrencyController {
    /**
     * Retrieves the default currency.
     *
     * @return the default currency
     * @since 3.0.0
     */
    @Contract(pure = true)
    Currency getDefaultCurrency();

    /**
     * Retrieves all currencies managed by this controller, including the default currency.
     * <p>
     * Providers without {@link EconomyCapability#MULTI_CURRENCY MULTI_CURRENCY}
     * return a singleton set containing the default currency.
     *
     * @return an unmodifiable set of currencies
     * @since 3.0.0
     */
    @Unmodifiable
    default Set<Currency> getCurrencies() {
        return Set.of(getDefaultCurrency());
    }

    /**
     * Retrieves a currency by its name.
     * <p>
     * Providers without {@link EconomyCapability#MULTI_CURRENCY MULTI_CURRENCY}
     * only match the default currency.
     *
     * @param name the name of the currency to retrieve
     * @return an optional containing the currency, or empty if not found
     * @since 3.0.0
     */
    default Optional<Currency> getCurrency(final String name) {
        final var currency = getDefaultCurrency();
        return currency.getName().equals(name) ? Optional.of(currency) : Optional.empty();
    }

    /**
     * Creates a new currency from the provided data.
     *
     * @param data the currency data
     * @return the newly created currency
     * @throws CapabilityException      if multi-currency is not supported
     * @throws IllegalArgumentException if a currency with the same name already exists
     * @since 3.0.0
     */
    @Contract("_ -> new")
    Currency createCurrency(CurrencyData data) throws IllegalArgumentException;

    /**
     * Creates a new currency with no localized display names.
     *
     * @param name             the unique name of the currency
     * @param symbol           the currency symbol
     * @param fractionalDigits the number of fractional digits
     * @return the newly created currency
     * @throws CapabilityException      if multi-currency is not supported
     * @throws IllegalArgumentException if a currency with the same name already exists
     * @since 3.0.0
     */
    @Contract("_, _, _ -> new")
    default Currency createCurrency(final String name, final Component symbol, final int fractionalDigits) throws IllegalArgumentException {
        return createCurrency(CurrencyData.of(name, symbol, fractionalDigits));
    }

    /**
     * Deletes the specified currency.
     *
     * @param currency the currency to delete
     * @return {@code true} if the currency was successfully deleted
     * @throws CapabilityException if multi-currency is not supported
     * @since 3.0.0
     */
    default boolean deleteCurrency(final Currency currency) {
        return deleteCurrency(currency.getName());
    }

    /**
     * Deletes a currency by name.
     *
     * @param name the name of the currency to delete
     * @return {@code true} if the currency was successfully deleted
     * @throws CapabilityException if multi-currency is not supported
     * @since 3.0.0
     */
    boolean deleteCurrency(String name);
}
