package net.thenextlvl.service.api.economy.bank;

import net.thenextlvl.service.api.economy.Account;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;

public interface Bank extends Account {
    @Unmodifiable
    Set<UUID> getMembers();

    String getName();

    default boolean addMember(OfflinePlayer player) {
        return addMember(player.getUniqueId());
    }

    boolean addMember(UUID uuid);

    default boolean isMember(OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    boolean isMember(UUID uuid);

    default boolean removeMember(OfflinePlayer player) {
        return removeMember(player.getUniqueId());
    }

    boolean removeMember(UUID uuid);

    default boolean setOwner(OfflinePlayer player) {
        return setOwner(player.getUniqueId());
    }

    boolean setOwner(UUID uuid);
}
