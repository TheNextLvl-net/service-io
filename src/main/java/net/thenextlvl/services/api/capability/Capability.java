package net.thenextlvl.services.api.capability;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

public interface Capability {
    @ApiStatus.OverrideOnly
    void disable(CapabilityController controller);

    @ApiStatus.OverrideOnly
    void enable(CapabilityController controller);

    Plugin getPlugin();
}
