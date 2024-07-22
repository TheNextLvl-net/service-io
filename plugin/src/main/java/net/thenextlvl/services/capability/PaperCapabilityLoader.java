package net.thenextlvl.services.capability;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.services.api.capability.Capability;
import net.thenextlvl.services.api.capability.CapabilityController;
import net.thenextlvl.services.api.capability.CapabilityLoader;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class PaperCapabilityLoader implements CapabilityLoader {
    private final Set<Capability> capabilities = new HashSet<>();
    private final CapabilityController controller;

    @Override
    public void disableCapabilities(Plugin plugin) {
        controller.registry().capabilities(plugin)
                .forEach(this::disableCapability);
    }

    @Override
    public void disableCapability(Capability capability) {
        try {
            capability.disable(controller);
            controller.owner().getComponentLogger().info(
                    "Successfully unhooked from {}",
                    capability.getPlugin().getName()
            );
        } catch (Exception e) {
            controller.owner().getComponentLogger().error(
                    "Failed to unhook from {}",
                    capability.getPlugin().getName(), e
            );
        }
    }

    @Override
    public void enableCapabilities(Plugin plugin) {
        controller.registry().capabilityConstructors(plugin).forEach(constructor ->
                enableCapability(constructor.construct(controller.provider(), plugin)));
    }

    @Override
    public void enableCapability(Capability capability) {
        try {
            capability.enable(controller);
            controller.owner().getComponentLogger().info(
                    "Successfully hooked into {}",
                    capability.getPlugin().getName()
            );
        } catch (Exception e) {
            controller.owner().getComponentLogger().error(
                    "Failed to hook into {}",
                    capability.getPlugin().getName(), e
            );
        }
    }
}
