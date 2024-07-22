package net.thenextlvl.services.api.capability;

import org.bukkit.plugin.Plugin;

import java.util.Set;

public interface CapabilityRegistry {
    boolean isRegistered(CapabilityConstructor<?> constructor);

    boolean isRegistered(Capability capability);

    void registerCapability(CapabilityConstructor<?> constructor);

    void unregisterCapability(Capability capability);

    void unregisterCapability(CapabilityConstructor<?> constructor);

    Set<Capability> capabilities();

    Set<Capability> capabilities(Plugin plugin);

    Set<CapabilityConstructor<?>> capabilityConstructors();

    Set<CapabilityConstructor<?>> capabilityConstructors(Plugin plugin);

    void unregisterCapabilities();

    void unregisterCapabilities(Plugin plugin);
}
