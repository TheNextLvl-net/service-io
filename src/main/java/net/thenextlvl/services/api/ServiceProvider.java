package net.thenextlvl.services.api;

import net.thenextlvl.services.api.chat.ChatController;
import net.thenextlvl.services.api.economy.EconomyController;
import net.thenextlvl.services.api.permission.GroupController;
import org.bukkit.plugin.Plugin;

/**
 * This class provides methods for retrieving instances of different controllers.
 */
public interface ServiceProvider extends Plugin {
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
