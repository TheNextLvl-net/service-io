package net.thenextlvl.service.api.capability;

import net.kyori.adventure.key.Keyed;

/**
 * Represents a specific functionality or feature that can be supported or provided
 * by a system, service, or component.
 * A {@code Capability} serves as a key to identify and interact with a particular
 * feature or capability, allowing for modular and extensible design.
 * <p>
 * This interface extends {@link Keyed}, which provides a mechanism for providing
 * unique identifiers for each capability.
 * <p>
 * Implementations of this interface can be used in conjunction with capability
 * providers or related systems to organize and query available functionalities.
 *
 * @since 2.2.0
 */
public interface Capability extends Keyed {
}
