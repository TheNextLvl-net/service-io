package net.thenextlvl.services.api.capability;

import org.bukkit.plugin.Plugin;

public interface CapabilityLoader {
    void disableCapabilities(Plugin plugin);

    void disableCapability(Capability capability);

    void enableCapabilities(Plugin plugin);

    void enableCapability(Capability capability);
}
