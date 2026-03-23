package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.economy.currency.Currency;
import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;

/**
 * Represents the result of an economic transaction such as a deposit, withdrawal, or balance change.
 *
 * @param type     the type of transaction that was performed
 * @param currency the currency involved in the transaction
 * @param amount   the amount that was transacted
 * @param balance  the account balance after the transaction
 * @param status   the outcome status of the transaction
 * @since 3.0.0
 */
public record TransactionResult(
        Type type,
        Currency currency,
        BigDecimal amount,
        BigDecimal balance,
        Status status
) {
    /**
     * Returns whether the transaction was successful.
     *
     * @return {@code true} if the status is {@link Status#SUCCESS}
     */
    @Contract(pure = true)
    public boolean successful() {
        return status == Status.SUCCESS;
    }

    /**
     * The type of transaction that was performed.
     */
    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        SET
    }

    /**
     * The outcome status of a transaction.
     */
    public enum Status {
        /**
         * The transaction completed successfully.
         */
        SUCCESS,
        /**
         * The account does not have sufficient funds.
         */
        INSUFFICIENT_FUNDS,
        /**
         * The account cannot hold the specified currency.
         */
        CURRENCY_NOT_SUPPORTED,
        /**
         * The transaction failed for an unspecified reason.
         */
        FAILURE
    }
}
