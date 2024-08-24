package net.thenextlvl.service.api.economy;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Account is an interface representing a financial account.
 */
public interface Account {
    /**
     * Returns the UUID of the owner of this account.
     *
     * @return the UUID of the owner
     */
    UUID getOwner();

    /**
     * Deposits the specified amount into the account balance.
     *
     * @param amount the amount to be deposited
     * @return the new balance after the deposit
     */
    BigDecimal deposit(Number amount);

    /**
     * Retrieves the balance of the account.
     *
     * @return the balance of the account
     */
    BigDecimal getBalance();

    /**
     * Withdraws the specified amount from the account balance.
     *
     * @param amount the amount to be withdrawn
     * @return the new balance after the withdrawal
     */
    BigDecimal withdraw(Number amount);
}
