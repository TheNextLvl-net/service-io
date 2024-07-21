package net.thenextlvl.services.api.capability;

public record Capability<C, I extends C>(Class<C> commonInterface, I implementation) {
}
