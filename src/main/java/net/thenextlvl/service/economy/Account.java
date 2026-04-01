package net.thenextlvl.service.economy;

import net.thenextlvl.service.economy.currency.Currency;
import net.thenextlvl.service.economy.currency.CurrencyController;
import org.bukkit.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a financial account belonging to a player.
 * <p>
 * All balance operations require an explicit {@link Currency} parameter to avoid
 * ambiguity. Use {@link EconomyController#getCurrencyController()} to obtain
 * the {@link CurrencyController#getDefaultCurrency() default currency}.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
 * @since 3.0.0
 */
public interface Account {
    /**
     * Returns the UUID of the owner of this account.
     *
     * @return the UUID of the owner
     */
    UUID getOwner();

    /**
     * Returns the world associated with this account, if any.
     *
     * @return an optional containing the world, or empty for global accounts
     */
    Optional<World> getWorld();

    /**
     * Retrieves the balance of the account for the specified currency.
     *
     * @param currency the currency for which the balance is to be retrieved
     * @return the balance of the account
     * @throws IllegalArgumentException if the account cannot hold the specified currency
     */
    BigDecimal getBalance(Currency currency);

    /**
     * Deposits the specified amount of the given currency into the account.
     *
     * @param amount   the amount to deposit
     * @param currency the currency to deposit
     * @return the result of the transaction
     */
    TransactionResult deposit(Number amount, Currency currency);

    /**
     * Withdraws the specified amount of the given currency from the account.
     *
     * @param amount   the amount to withdraw
     * @param currency the currency to withdraw
     * @return the result of the transaction
     */
    TransactionResult withdraw(Number amount, Currency currency);

    /**
     * Sets the balance of the account for the given currency.
     *
     * @param balance  the new balance
     * @param currency the currency of the balance
     * @return the result of the transaction
     */
    TransactionResult setBalance(Number balance, Currency currency);

    /**
     * Checks if the account can hold the specified currency.
     *
     * @param currency the currency to check
     * @return {@code true} if the account supports the currency
     */
    boolean canHold(Currency currency);
}
