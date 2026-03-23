package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Account is an interface representing a financial account.
 *
 * @since 1.0.0
 */
public interface Account extends Comparable<Account> {
    /**
     * Deposits the specified amount into the account balance.
     *
     * @param amount the amount to be deposited
     * @return the new balance after the deposit
     */
    default BigDecimal deposit(final Number amount) {
        return deposit(amount, null);
    }

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
    default BigDecimal deposit(final Number amount, @Nullable final Currency currency) {
        return setBalance(getBalance(currency).add(BigDecimal.valueOf(amount.doubleValue())), currency);
    }

    /**
     * Retrieves the balance of the account.
     *
     * @return the balance of the account
     */
    default BigDecimal getBalance() {
        return getBalance(null);
    }

    /**
     * Retrieves the balance of the account for the specified currency.
     *
     * @param currency the currency for which the balance is to be retrieved
     * @return the balance of the account for the specified currency
     * @since 3.0.0
     */
    BigDecimal getBalance(@Nullable Currency currency);

    /**
     * Withdraws the specified amount from the account balance.
     *
     * @param amount the amount to be withdrawn
     * @return the new balance after the withdrawal
     */
    default BigDecimal withdraw(final Number amount) {
        return withdraw(amount, null);
    }

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
    default BigDecimal withdraw(final Number amount, @Nullable final Currency currency) {
        return setBalance(getBalance(currency).subtract(BigDecimal.valueOf(amount.doubleValue())), currency);
    }

    /**
     * Returns an optional containing the world associated with this account.
     *
     * @return an {@code Optional<World>} containing the world associated with this account, or empty
     */
    Optional<World> getWorld();

    /**
     * Returns the UUID of the owner of this account.
     *
     * @return the UUID of the owner
     */
    UUID getOwner();

    /**
     * Compares this account to the specified account based on their balance.
     *
     * @param account the account to be compared
     * @return a negative integer, zero, or a positive integer if this account is
     * less than, equal to, or greater than the specified account
     */
    @Override
    default int compareTo(final Account account) {
        return compareTo(account, null);
    }

    /**
     * Compares this account with another account based on their balances in the specified currency.
     *
     * @param account  the account to be compared
     * @param currency the currency in which the balances should be compared
     * @return a negative integer, zero, or a positive integer if this account's balance
     * is less than, equal to, or greater than the specified account's balance
     * @since 3.0.0
     */
    default int compareTo(final Account account, @Nullable final Currency currency) {
        return getBalance(currency).compareTo(account.getBalance(currency));
    }

    /**
     * Sets the balance of the account to the specified value.
     *
     * @param balance the new balance of the account
     * @since 3.0.0
     */
    default BigDecimal setBalance(final Number balance) {
        return setBalance(balance, null);
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
    BigDecimal setBalance(Number balance, @Nullable Currency currency);

    /**
     * Checks if the account can hold the specified currency.
     *
     * @param currency the currency to check support for
     * @return {@code true} if the account can hold the specified currency, otherwise {@code false}
     * @since 3.0.0
     */
    default boolean canHold(@Nullable final Currency currency) {
        return true;
    }
}
