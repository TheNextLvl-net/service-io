package net.thenextlvl.service;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

public interface Controller {
    /**
     * Retrieves the plugin associated with the controller.
     *
     * @return the plugin instance linked to the controller.
     * @since 2.2.1
     */
    @Contract(pure = true)
    Plugin getPlugin();

    /**
     * Retrieves the name associated with the controller.
     *
     * @return the name of the controller.
     * @since 2.2.1
     */
    @Contract(pure = true)
    String getName();
}
