package net.thenextlvl.service.economy;

import net.thenextlvl.service.economy.currency.Currency;
import org.jetbrains.annotations.Contract;

/**
 * Represents the result of an economic transaction such as a deposit, withdrawal, or balance change.
 *
 * @param currency the currency involved in the transaction
 * @param amount   the amount that was transacted
 * @param balance  the account balance after the transaction
 * @param status   the outcome status of the transaction
 * @since 3.0.0
 */
public record TransactionResult(
        Currency currency,
        Number amount,
        Number balance,
        Status status
) {
    /**
     * Creates a {@code TransactionResult} indicating that the specified currency is not supported.
     *
     * @param currency the currency that is not supported
     * @return a {@code TransactionResult} with the status set to {@code Status.CURRENCY_NOT_SUPPORTED},
     * the amount set to {@code 0}, and the balance set to {@code 0}
     * @since 3.0.0
     */
    public static TransactionResult unsupported(final Currency currency) {
        return new TransactionResult(currency, 0, 0, Status.CURRENCY_NOT_SUPPORTED);
    }

    /**
     * Returns whether the transaction was successful.
     *
     * @return {@code true} if the status is {@link Status#SUCCESS}
     * @since 3.0.0
     */
    @Contract(pure = true)
    public boolean successful() {
        return status == Status.SUCCESS;
    }

    /**
     * The outcome status of a transaction.
     *
     * @since 3.0.0
     */
    public enum Status {
        /**
         * The transaction completed successfully.
         *
         * @since 3.0.0
         */
        SUCCESS,
        /**
         * The account does not have sufficient funds.
         *
         * @since 3.0.0
         */
        INSUFFICIENT_FUNDS,
        /**
         * The account cannot hold the specified currency.
         *
         * @since 3.0.0
         */
        CURRENCY_NOT_SUPPORTED,
        /**
         * The transaction failed for an unspecified reason.
         *
         * @since 3.0.0
         */
        FAILURE
    }
}
