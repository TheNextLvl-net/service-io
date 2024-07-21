package net.thenextlvl.services.economy;

import org.bukkit.World;

/**
 * WorldAccount is an interface representing a financial account associated with a specific world.
 * It extends the Account interface.
 */
public interface WorldAccount extends Account {
    /**
     * Retrieves the World associated with this account.
     *
     * @return the World associated with this account
     */
    World getWorld();
}
