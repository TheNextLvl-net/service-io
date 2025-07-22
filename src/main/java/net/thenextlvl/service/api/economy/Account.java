package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Account is an interface representing a financial account.
 *
 * @since 1.0.0
 */
@NullMarked
public interface Account {
    /**
     * Deposits the specified amount of the given currency into the account balance.
     * <p>
     * Returns {@link BigDecimal#ZERO} if {@link #canHold(Currency)} returns {@code false}
     *
     * @param amount   the amount to be deposited
     * @param currency the currency that is being deposited
     * @return the new balance after the deposit
     * @since 3.0.0
     */
    default BigDecimal deposit(Number amount, Currency currency) {
        return setBalance(getBalance(currency).add(BigDecimal.valueOf(amount.doubleValue())), currency);
    }

    /**
     * Retrieves the balance of the account for the specified currency.
     *
     * @param currency the currency for which the balance is to be retrieved
     * @return the balance of the account for the specified currency
     * @since 3.0.0
     */
    BigDecimal getBalance(Currency currency);

    /**
     * Withdraws the specified amount of the given currency from the account balance.
     * <p>
     * Returns {@link BigDecimal#ZERO} if {@link #canHold(Currency)} returns {@code false}
     *
     * @param amount   the amount to be withdrawn
     * @param currency the currency in which the withdrawal is to be made
     * @return the new balance after the withdrawal
     * @since 3.0.0
     */
    default BigDecimal withdraw(Number amount, Currency currency) {
        return setBalance(getBalance(currency).subtract(BigDecimal.valueOf(amount.doubleValue())), currency);
    }

    /**
     * Returns the world associated with this account.
     *
     * @return an optional containing the world associated with this account, or empty
     */
    Optional<World> getWorld();

    /**
     * Returns the account owner's uuid.
     *
     * @return the account owner's uuid
     */
    UUID getOwner();

    /**
     * Compares this account with another account based on their balances in the specified currency.
     *
     * @param account  the account to be compared
     * @param currency the currency in which the balances should be compared
     * @return a negative integer, zero, or a positive integer if this account's balance
     * is less than, equal to, or greater than the specified account's balance
     * @since 3.0.0
     */
    default int compareTo(Account account, Currency currency) {
        return getBalance(currency).compareTo(account.getBalance(currency));
    }

    /**
     * Sets the balance of the account to the specified value in the given currency.
     * <p>
     * Returns {@link BigDecimal#ZERO} if {@link #canHold(Currency)} returns {@code false}
     *
     * @param balance  the new balance to be set
     * @param currency the currency of the balance
     * @return the new balance after the operation
     * @see #canHold(Currency)
     * @since 3.0.0
     */
    BigDecimal setBalance(Number balance, Currency currency);

    /**
     * Checks if the account can hold the specified currency.
     *
     * @param currency the currency to check support for
     * @return {@code true} if the account can hold the specified currency, otherwise {@code false}
     * @since 3.0.0
     */
    boolean canHold(Currency currency);
}
