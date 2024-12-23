package net.thenextlvl.service.api;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

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
