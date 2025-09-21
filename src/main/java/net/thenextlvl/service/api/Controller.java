package net.thenextlvl.service.api;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * The `Controller` interface provides methods to retrieve basic information
 * about the controller, such as the associated plugin and name.
 *
 * @since 2.2.1
 */
@NullMarked
public interface Controller {
    /**
     * Retrieves the plugin associated with the controller.
     *
     * @return the plugin instance linked to the controller.
     */
    @Contract(pure = true)
    Plugin getPlugin();

    /**
     * Retrieves the name associated with the controller.
     *
     * @return the name of the controller.
     */
    @Contract(pure = true)
    String getName();
}
