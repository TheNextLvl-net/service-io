package net.thenextlvl.services.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.services.ServicePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

@RequiredArgsConstructor
public class PluginListener implements Listener {
    private final ServicePlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        plugin.capabilityControllers().values().forEach(controller ->
                controller.loader().enableCapabilities(event.getPlugin()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginDisableEvent event) {
        plugin.capabilityControllers().values().forEach(controller ->
                controller.loader().disableCapabilities(event.getPlugin()));
    }
}
