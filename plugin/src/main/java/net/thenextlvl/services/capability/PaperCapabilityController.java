package net.thenextlvl.services.capability;

import net.thenextlvl.services.api.capability.Capability;
import net.thenextlvl.services.api.capability.CapabilityController;

public class PaperCapabilityController implements CapabilityController {
    @Override
    public <C, I extends C> C registerCapability(String plugin, Capability<C, I> capability) {
        return null;
    }
}
