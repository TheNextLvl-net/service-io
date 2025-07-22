package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

/**
 * The Bank interface represents a financial entity that can be owned and hold members.
 * It extends the Account interface, providing additional functionality specific
 * to banking, such as depositing or withdrawing money.
 *
 * @since 1.0.0
 */
@NullMarked
public interface Bank extends Account {
    /**
     * Retrieves a set of UUIDs representing the members of the bank.
     *
     * @return an unmodifiable set containing the UUIDs of the members.
     * @apiNote does not contain the uuid of the owner
     */
    @Unmodifiable
    Set<UUID> getMembers();

    /**
     * Retrieves the name associated with the bank.
     *
     * @return the name of the bank.
     */
    @Contract(pure = true)
    String getName();

    /**
     * Adds a member to the bank.
     *
     * @param player the player to be added as a member
     * @return {@code true} if the player was successfully added as a member, otherwise {@code false}
     */
    default boolean addMember(OfflinePlayer player) {
        return addMember(player.getUniqueId());
    }

    /**
     * Adds a member to the bank.
     *
     * @param uuid the uuid of the member to be added
     * @return {@code true} if the member was successfully added, otherwise {@code false}
     */
    boolean addMember(UUID uuid);

    /**
     * Checks if the specified player is a member of the bank.
     *
     * @param player the player to check for membership
     * @return {@code true} if the player is a member of the bank, otherwise {@code false}
     * @apiNote returns {@code false} on the owner
     */
    default boolean isMember(OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    /**
     * Checks if the specified uuid is associated with a member of the bank.
     *
     * @param uuid the uuid of the member to check for membership
     * @return {@code true} if the uuid corresponds to a member of the bank, otherwise {@code false}
     * @apiNote returns {@code false} on the owner
     */
    boolean isMember(UUID uuid);

    /**
     * Removes a member from the bank.
     *
     * @param player the player to be removed as a member
     * @return {@code true} if the member was successfully removed, otherwise {@code false}
     */
    default boolean removeMember(OfflinePlayer player) {
        return removeMember(player.getUniqueId());
    }

    /**
     * Removes a member from the bank using the specified uuid.
     *
     * @param uuid the uuid of the member to be removed
     * @return {@code true} if the member was successfully removed, otherwise {@code false}
     */
    boolean removeMember(UUID uuid);

    /**
     * Sets the specified player as the owner of the bank.
     *
     * @param player the player to be set as the owner
     * @return {@code true} if the player was successfully set as the owner, otherwise {@code false}
     */
    default boolean setOwner(OfflinePlayer player) {
        return setOwner(player.getUniqueId());
    }

    /**
     * Sets the owner of the bank to the specified uuid.
     *
     * @param uuid the uuid of the new owner
     * @return {@code true} if the owner was successfully set, otherwise {@code false}
     */
    boolean setOwner(UUID uuid);
}
