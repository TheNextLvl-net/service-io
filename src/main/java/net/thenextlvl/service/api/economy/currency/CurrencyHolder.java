package net.thenextlvl.service.api.economy.currency;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents an entity capable of handling currencies in an economy system.
 * This interface provides methods for formatting, retrieving, creating,
 * and deleting currencies, as well as determining support for multiple currencies.
 *
 * @since 3.0.0
 */
public interface CurrencyHolder {
    /**
     * Retrieves all currencies managed by the currency holder,
     * including the {@link #getDefaultCurrency() default currency}.
     *
     * @return an unmodifiable set of currencies
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     */
    default @Unmodifiable Set<Currency> getCurrencies() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves a currency by its name.
     *
     * @param name the name of the currency to retrieve
     * @return an {@code Optional} containing the currency, or empty
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     */
    default Optional<Currency> getCurrency(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if a currency with the specified name exists.
     *
     * @param name the name of the currency to check for existence
     * @return {@code true} if the currency exists, otherwise {@code false}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     */
    default boolean hasCurrency(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a new currency by configuring a {@link Currency.Builder}.
     *
     * @param name    the name of the new currency
     * @param builder a consumer to configure the {@link Currency.Builder} for currency creation
     * @return the newly created {@link Currency}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     * @throws IllegalArgumentException      if a currency with the same name already exists
     */
    @Contract("_, _ -> new")
    default Currency createCurrency(String name, Consumer<Currency.Builder> builder) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a new currency using the specified builder.
     * <p>
     * This method enables modifying existing currencies by utilizing a
     * pre-configured builder to create a new currency.
     *
     * @param builder the {@link Currency.Builder} containing the configuration for the currency creation
     * @return the newly created {@link Currency}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     * @throws IllegalArgumentException      if a currency with the same name already exists
     */
    default Currency createCurrency(Currency.Builder builder) {
        return createCurrency(builder.name(), delegate -> {
            builder.displayNamesPlural().forEach(delegate::displayNamePlural);
            builder.displayNamesSingular().forEach(delegate::displayNameSingular);
            builder.fractionalDigits().ifPresent(delegate::fractionalDigits);
            builder.symbol().ifPresent(delegate::symbol);
        });
    }

    /**
     * Deletes the specified currency.
     *
     * @param currency the currency to delete
     * @return {@code true} if the currency was successfully deleted, otherwise {@code false}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     */
    default boolean deleteCurrency(Currency currency) {
        return deleteCurrency(currency.getName());
    }

    /**
     * Deletes a currency with the specified name.
     *
     * @param name the name of the currency to delete
     * @return {@code true} if the currency was successfully deleted, otherwise {@code false}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     */
    default boolean deleteCurrency(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves the default currency for this economy controller.
     *
     * @return the default currency
     */
    @Contract(pure = true)
    Currency getDefaultCurrency();

    /**
     * Determines whether the holder supports multiple currencies.
     *
     * @return {@code true} if multi-currency is supported, otherwise {@code false}
     * @implSpec If multiple currencies are supported, all respective methods have to be implemented.
     * @see #createCurrency(String, Consumer)
     * @see #deleteCurrency(String)
     * @see #getCurrencies()
     * @see #getCurrency(String)
     * @see #hasCurrency(String)
     */
    @Contract(pure = true)
    default boolean hasMultiCurrencySupport() {
        return false;
    }
}
