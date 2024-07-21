package net.thenextlvl.services;

import net.thenextlvl.services.chat.ChatController;
import net.thenextlvl.services.economy.EconomyController;
import net.thenextlvl.services.permission.GroupController;

/**
 * The ServiceProvider interface provides methods to retrieve instances of different controllers.
 */
public interface ServiceProvider {
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
