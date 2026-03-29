package net.thenextlvl.service.api.capability;

import org.jetbrains.annotations.Contract;

/**
 * Represents a specific functionality or feature that can be supported or provided
 * by a system, service, or component.
 * A {@code Capability} serves as a human-friendly way to identify a particular feature or capability.
 * <p>
 * Implementations of this interface can be used in conjunction with capability
 * providers or related systems to organize and query available functionalities.
 */
@FunctionalInterface
public interface Capability {
    @Contract(pure = true)
    String name();
}
