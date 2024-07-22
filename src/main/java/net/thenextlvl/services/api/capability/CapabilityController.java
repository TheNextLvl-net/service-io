package net.thenextlvl.services.api.capability;

import net.thenextlvl.services.api.ServiceProvider;
import org.bukkit.plugin.Plugin;

public interface CapabilityController {
    CapabilityLoader loader();

    CapabilityRegistry registry();

    Plugin owner();

    ServiceProvider provider();
}
