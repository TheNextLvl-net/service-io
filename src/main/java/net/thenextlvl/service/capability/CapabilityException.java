package net.thenextlvl.service.capability;

import org.bukkit.plugin.Plugin;

/**
 * An exception that indicates a problem related to a specific {@link Capability}.
 * This exception is typically thrown when there is an issue or unsupported operation
 * associated with a particular capability in the system.
 */
public class CapabilityException extends RuntimeException {
    private final Capability capability;

    /**
     * Creates a new {@code CapabilityException} with the related capability that caused this exception.
     *
     * @param plugin     the {@link Plugin} instance that is missing the capability,
     * @param capability the {@link Capability} instance associated with this exception,
     *                   indicating the capability that caused the issue.
     * @since 3.0.0
     */
    public CapabilityException(final Plugin plugin, final Capability capability) {
        this(plugin.getName() + " is missing the capability: " + capability.name(), capability);
    }

    /**
     * Creates a new {@code CapabilityException} with the specified detail message and
     * the related capability that caused this exception.
     *
     * @param message    the detail message, providing additional information about the exception.
     * @param capability the {@link Capability} instance associated with this exception,
     *                   indicating the capability that caused the issue.
     */
    public CapabilityException(final String message, final Capability capability) {
        super(message);
        this.capability = capability;
    }

    /**
     * Retrieves the capability associated with this exception.
     *
     * @return the {@link Capability} instance that caused this exception,
     * representing a specific feature or limitation.
     */
    public Capability getCapability() {
        return this.capability;
    }
}
