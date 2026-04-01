package net.thenextlvl.service.economy.bank;

import net.thenextlvl.service.economy.Account;
import net.thenextlvl.service.economy.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a bank account that extends {@link Account} with ownership and membership management.
 *
 * @since 3.0.0
 */
public interface Bank extends Account {
    /**
     * Retrieves the name of the bank.
     *
     * @return the name of the bank
     */
    @Contract(pure = true)
    String getName();

    /**
     * Retrieves the UUIDs of all members of the bank (excluding the owner).
     *
     * @return an unmodifiable set of member UUIDs
     */
    @Unmodifiable
    Set<UUID> getMembers();

    /**
     * Adds a member to the bank.
     *
     * @param player the player to add
     * @return {@code true} if the player was added
     */
    default boolean addMember(final OfflinePlayer player) {
        return addMember(player.getUniqueId());
    }

    /**
     * Adds a member to the bank.
     *
     * @param uuid the UUID of the member to add
     * @return {@code true} if the member was added
     */
    boolean addMember(UUID uuid);

    /**
     * Checks if the specified player is a member (not the owner) of this bank.
     *
     * @param player the player to check
     * @return {@code true} if the player is a member
     */
    default boolean isMember(final OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    /**
     * Checks if the specified UUID is a member (not the owner) of this bank.
     *
     * @param uuid the UUID to check
     * @return {@code true} if the UUID corresponds to a member
     */
    boolean isMember(UUID uuid);

    /**
     * Removes a member from the bank.
     *
     * @param player the player to remove
     * @return {@code true} if the member was removed
     */
    default boolean removeMember(final OfflinePlayer player) {
        return removeMember(player.getUniqueId());
    }

    /**
     * Removes a member from the bank.
     *
     * @param uuid the UUID of the member to remove
     * @return {@code true} if the member was removed
     */
    boolean removeMember(UUID uuid);

    /**
     * Sets the owner of the bank.
     *
     * @param player the player to set as owner
     * @return {@code true} if the owner was changed
     */
    default boolean setOwner(final OfflinePlayer player) {
        return setOwner(player.getUniqueId());
    }

    /**
     * Sets the owner of the bank.
     *
     * @param uuid the UUID of the new owner
     * @return {@code true} if the owner was changed
     */
    boolean setOwner(UUID uuid);

    /**
     * Checks whether the specified player can deposit into this bank.
     *
     * @param player   the player attempting the deposit
     * @param amount   the amount to deposit
     * @param currency the currency of the deposit
     * @return {@code true} if the deposit is allowed
     */
    default boolean canDeposit(final OfflinePlayer player, final Number amount, final Currency currency) {
        return canDeposit(player.getUniqueId(), amount, currency);
    }

    /**
     * Checks whether the specified UUID can deposit into this bank.
     *
     * @param uuid     the UUID of the player
     * @param amount   the amount to deposit
     * @param currency the currency of the deposit
     * @return {@code true} if the deposit is allowed
     */
    default boolean canDeposit(final UUID uuid, final Number amount, final Currency currency) {
        return getOwner().equals(uuid) || isMember(uuid);
    }

    /**
     * Checks whether the specified player can withdraw from this bank.
     *
     * @param player   the player attempting the withdrawal
     * @param amount   the amount to withdraw
     * @param currency the currency of the withdrawal
     * @return {@code true} if the withdrawal is allowed
     */
    default boolean canWithdraw(final OfflinePlayer player, final Number amount, final Currency currency) {
        return canWithdraw(player.getUniqueId(), amount, currency);
    }

    /**
     * Checks whether the specified UUID can withdraw from this bank.
     *
     * @param uuid     the UUID of the player
     * @param amount   the amount to withdraw
     * @param currency the currency of the withdrawal
     * @return {@code true} if the withdrawal is allowed
     */
    default boolean canWithdraw(final UUID uuid, final Number amount, final Currency currency) {
        return getOwner().equals(uuid);
    }
}
