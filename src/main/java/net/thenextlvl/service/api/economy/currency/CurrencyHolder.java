package net.thenextlvl.service.api.economy.currency;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents an entity capable of handling currencies in an economy system.
 * This interface provides methods for formatting, retrieving, creating,
 * and deleting currencies, as well as determining support for multiple currencies.
 */
public interface CurrencyHolder {
    /**
     * Formats the specified amount as a string.
     *
     * @param amount the number amount to be formatted
     * @return the formatted amount as a string
     * @deprecated use {@link Currency#format(Number, Locale)}
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default String format(Number amount) {
        return PlainTextComponentSerializer.plainText().serialize(getDefaultCurrency().format(amount, Locale.US));
    }

    /**
     * Retrieves the number of fractional digits used for formatting currency amounts.
     *
     * @return the number of fractional digits used for formatting currency amounts
     * @deprecated use {@link Currency#getFractionalDigits()}
     */
    @Deprecated(forRemoval = true, since = "3.0.0")
    default int fractionalDigits() {
        return getDefaultCurrency().getFractionalDigits();
    }

    /**
     * Retrieves all currencies managed by the currency holder.
     *
     * @return an unmodifiable set of currencies
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     * @since 3.0.0
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
     * @since 3.0.0
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
     * @since 3.0.0
     */
    default boolean hasCurrency(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a new currency by configuring a {@link Currency.Builder}.
     * <p>
     * If a currency with the same name already exists, this method returns empty.
     *
     * @param name    the name of the new currency
     * @param builder a consumer to configure the {@link Currency.Builder} for currency creation
     * @return an optional containing the created {@link Currency}, or empty
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     * @since 3.0.0
     */
    @Contract("_, _ -> new")
    default Optional<Currency> createCurrency(String name, Consumer<Currency.Builder> builder) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Deletes a currency with the specified name.
     *
     * @param name the name of the currency to delete
     * @return {@code true} if the currency was successfully deleted, otherwise {@code false}
     * @throws UnsupportedOperationException if {@link #hasMultiCurrencySupport()} is {@code false}
     * @since 3.0.0
     */
    default boolean deleteCurrency(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves the default currency for this economy controller.
     *
     * @return the default currency
     * @since 3.0.0
     */
    @Contract(pure = true)
    Currency getDefaultCurrency();

    /**
     * Determines whether the economy controller supports multiple currencies.
     *
     * @return {@code true} if multi-currency is supported, otherwise {@code false}
     * @implSpec If multiple currencies are supported, all respective methods have to be implemented.
     * @see #createCurrency(String, Consumer)
     * @see #deleteCurrency(String)
     * @see #getCurrencies()
     * @see #getCurrency(String)
     * @see #hasCurrency(String)
     * @since 3.0.0
     */
    @Contract(pure = true)
    default boolean hasMultiCurrencySupport() {
        return false;
    }
}
