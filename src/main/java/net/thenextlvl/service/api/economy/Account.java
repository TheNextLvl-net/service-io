package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyHolder;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Account is an interface representing a financial account.
 */
@NullMarked
public interface Account extends Comparable<Account> {
    /**
     * Retrieves the associated {@code CurrencyHolder} for the account.
     *
     * @return the {@code CurrencyHolder} capable of managing currencies for the account
     */
    CurrencyHolder getController();

    /**
     * Deposits the specified amount into the account balance.
     *
     * @param amount the amount to be deposited
     * @return the new balance after the deposit
     * @deprecated use {@link #deposit(Number, Currency)}
     */
    @Deprecated(forRemoval = true, since = "2.4.0")
    default BigDecimal deposit(Number amount) {
        return deposit(amount, getController().getDefaultCurrency());
    }

    /**
     * Deposits the specified amount of the given currency into the account balance.
     *
     * @param amount   the amount to be deposited
     * @param currency the currency that is being deposited
     * @return the new balance after the deposit
     */
    BigDecimal deposit(Number amount, Currency currency);

    /**
     * Retrieves the balance of the account.
     *
     * @return the balance of the account
     * @deprecated use {@link #getBalance(Currency)}
     */
    @Deprecated(forRemoval = true, since = "2.4.0")
    default BigDecimal getBalance() {
        return getBalance(getController().getDefaultCurrency());
    }

    BigDecimal getBalance(Currency currency);

    /**
     * Withdraws the specified amount from the account balance.
     *
     * @param amount the amount to be withdrawn
     * @return the new balance after the withdrawal
     * @deprecated use {@link #withdraw(Number, Currency)}
     */
    @Deprecated(forRemoval = true, since = "2.4.0")
    default BigDecimal withdraw(Number amount) {
        return withdraw(amount, getController().getDefaultCurrency());
    }

    /**
     * Withdraws the specified amount of the given currency from the account balance.
     *
     * @param amount   the amount to be withdrawn
     * @param currency the currency in which the withdrawal is to be made
     * @return the new balance after the withdrawal
     */
    BigDecimal withdraw(Number amount, Currency currency);

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
     * @deprecated use {@link #compareTo(Account, Currency)}
     */
    @Override
    @Deprecated(forRemoval = true, since = "2.4.0")
    default int compareTo(Account account) {
        return compareTo(account, getController().getDefaultCurrency());
    }

    /**
     * Compares this account with another account based on their balances in the specified currency.
     *
     * @param account  the account to be compared
     * @param currency the currency in which the balances should be compared
     * @return a negative integer, zero, or a positive integer if this account's balance
     * is less than, equal to, or greater than the specified account's balance
     */
    default int compareTo(Account account, Currency currency) {
        return getBalance(currency).compareTo(account.getBalance(currency));
    }

    /**
     * Sets the balance of the account to the specified value.
     *
     * @param balance the new balance of the account
     * @deprecated use {@link #setBalance(Number, Currency)}
     */
    @Deprecated(forRemoval = true, since = "2.4.0")
    default void setBalance(Number balance) {
        setBalance(balance, getController().getDefaultCurrency());
    }

    /**
     * Sets the balance of the account to the specified value in the given currency.
     *
     * @param balance  the new balance to be set
     * @param currency the currency of the balance
     */
    void setBalance(Number balance, Currency currency);
}
