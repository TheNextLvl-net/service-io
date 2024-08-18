package net.thenextlvl.services.capability;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.services.api.capability.Capability;
import net.thenextlvl.services.api.capability.CapabilityConstructor;
import net.thenextlvl.services.api.capability.CapabilityController;
import net.thenextlvl.services.api.capability.CapabilityRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PaperCapabilityRegistry implements CapabilityRegistry {
    private final Set<CapabilityConstructor<?>> capabilityConstructors = new HashSet<>();
    private final Set<Capability> capabilities = new HashSet<>();
    private final CapabilityController controller;

    @Override
    public boolean isRegistered(CapabilityConstructor<?> constructor) {
        return capabilityConstructors.contains(constructor);
    }

    @Override
    public boolean isRegistered(Capability capability) {
        return capabilities.contains(capability);
    }

    @Override
    public void registerCapability(CapabilityConstructor<?> constructor) {
        Preconditions.checkState(capabilityConstructors.add(constructor),
                "Capability constructor is already registered");
        Arrays.stream(controller.owner().getServer().getPluginManager().getPlugins())
                .filter(Plugin::isEnabled)
                .filter(constructor::isCapable)
                .map(plugin -> constructor.construct(controller.provider(), plugin))
                .forEach(capability -> controller.loader().enableCapability(capability));
    }

    @Override
    public void unregisterCapability(Capability capability) {
        Preconditions.checkState(capabilities.remove(capability),
                "Capability is not registered");
        controller.loader().disableCapability(capability);
    }

    @Override
    public void unregisterCapability(CapabilityConstructor<?> constructor) {
        Preconditions.checkState(capabilityConstructors.remove(constructor),
                "Capability constructor is not registered");
    }

    @Override
    public Set<Capability> capabilities() {
        return Set.copyOf(capabilities);
    }

    @Override
    public Set<Capability> capabilities(Plugin plugin) {
        return capabilities.stream()
                .filter(capability -> plugin.equals(capability.getPlugin()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<CapabilityConstructor<?>> capabilityConstructors() {
        return Set.copyOf(capabilityConstructors);
    }

    @Override
    public Set<CapabilityConstructor<?>> capabilityConstructors(Plugin plugin) {
        return capabilityConstructors().stream()
                .filter(constructor -> constructor.isCapable(plugin))
                .collect(Collectors.toSet());
    }

    @Override
    public void unregisterCapabilities() {
        capabilities().forEach(this::unregisterCapability);
    }

    @Override
    public void unregisterCapabilities(Plugin plugin) {
        capabilities(plugin).forEach(this::unregisterCapability);
    }
}
