package net.thenextlvl.services.api;

import net.thenextlvl.services.api.capability.CapabilityController;
import net.thenextlvl.services.api.chat.ChatController;
import net.thenextlvl.services.api.economy.EconomyController;
import net.thenextlvl.services.api.permission.GroupController;

/**
 * The ServiceProvider interface provides methods to retrieve instances of different controllers.
 */
public interface ServiceProvider {
    /**
     * Retrieves the capability controller.
     *
     * @return the capability controller instance
     */
    CapabilityController capabilityController();

    /**
     * Retrieves an instance of the chat controller.
     *
     * @return the chat controller instance
     */
    ChatController chatController();

    /**
     * Retrieves an instance of the economy controller.
     *
     * @return the economy controller instance
     */
    EconomyController economyController();

    /**
     * Retrieves an instance of the group controller.
     *
     * @return the group controller instance
     */
    GroupController groupController();
}
