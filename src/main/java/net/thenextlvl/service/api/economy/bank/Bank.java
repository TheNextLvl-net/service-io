package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.economy.Account;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

/**
 * The Bank interface represents a financial entity that can be owned and hold members.
 * It extends the Account interface, providing additional functionality specific
 * to banking, such as depositing or withdrawing money.
 */
@NullMarked
public interface Bank extends Account {
    /**
     * Retrieves a set of UUIDs representing the members of the bank.
     *
     * @return an unmodifiable set containing the UUIDs of the members.
     */
    @Unmodifiable
    Set<UUID> getMembers();

    /**
     * Retrieves the name associated with the bank.
     *
     * @return the name of the bank.
     */
    String getName();

    /**
     * Adds a member to the bank.
     *
     * @param player the OfflinePlayer object representing the player to be added as a member
     * @return true if the player was successfully added as a member, false otherwise
     */
    default boolean addMember(OfflinePlayer player) {
        return addMember(player.getUniqueId());
    }

    /**
     * Adds a member to the bank.
     *
     * @param uuid the UUID of the member to be added
     * @return true if the member was successfully added, false otherwise
     */
    boolean addMember(UUID uuid);

    /**
     * Checks if the specified player is a member of the bank.
     *
     * @param player the OfflinePlayer object representing the player to check for membership
     * @return true if the player is a member of the bank, false otherwise
     */
    default boolean isMember(OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    /**
     * Checks if the specified UUID is associated with a member of the bank.
     *
     * @param uuid the UUID of the member to check for membership
     * @return true if the UUID corresponds to a member of the bank, false otherwise
     */
    boolean isMember(UUID uuid);

    /**
     * Removes a member from the bank.
     *
     * @param player the OfflinePlayer object representing the player to be removed as a member
     * @return true if the member was successfully removed, false otherwise
     */
    default boolean removeMember(OfflinePlayer player) {
        return removeMember(player.getUniqueId());
    }

    /**
     * Removes a member from the bank using the specified UUID.
     *
     * @param uuid the UUID of the member to be removed
     * @return true if the member was successfully removed, false otherwise
     */
    boolean removeMember(UUID uuid);

    /**
     * Sets the specified OfflinePlayer as the owner of the bank.
     *
     * @param player the OfflinePlayer object representing the player to be set as the owner
     * @return true if the player was successfully set as the owner, false otherwise
     */
    default boolean setOwner(OfflinePlayer player) {
        return setOwner(player.getUniqueId());
    }

    /**
     * Sets the owner of the bank to the specified UUID.
     *
     * @param uuid the UUID of the new owner
     * @return true if the owner was successfully set, false otherwise
     */
    boolean setOwner(UUID uuid);
}
