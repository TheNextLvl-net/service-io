package net.thenextlvl.services.api.capability;

import net.thenextlvl.services.api.ServiceProvider;
import org.bukkit.plugin.Plugin;

public interface CapabilityConstructor<C extends Capability> {
    // safely perform your checks here
    // this method cannot throw exceptions
    boolean isCapable(Plugin plugin);

    // the plugin is the instance of the plugin that provides the capability
    // construct your object which cannot throw exceptions either
    C construct(ServiceProvider serviceProvider, Plugin plugin);
}
