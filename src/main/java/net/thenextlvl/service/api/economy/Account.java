package net.thenextlvl.service.api.economy;

import org.bukkit.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Account is an interface representing a financial account.
 */
public interface Account extends Comparable<Account> {
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
    default int compareTo(Account account) {
        return getBalance().compareTo(account.getBalance());
    }
}
