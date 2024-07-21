package net.thenextlvl.services.api.capability;

public interface CapabilityController {
    <C, I extends C> C registerCapability(String plugin, Capability<C, I> capability);
}
