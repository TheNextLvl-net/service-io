package net.thenextlvl.service.api.capability;

import org.jspecify.annotations.Nullable;

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
     * @param capability the {@link Capability} instance associated with this exception,
     *                   indicating the capability that caused the issue.
     */
    public CapabilityException(final Capability capability) {
        super();
        this.capability = capability;
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
     * Constructs a new {@code CapabilityException} with a specified detail message, cause,
     * and the associated capability that led to the exception.
     *
     * @param message    the detail message, providing additional information about the exception.
     * @param cause      the cause of the exception, which may be {@code null} to indicate no specific cause.
     * @param capability the {@link Capability} instance associated with this exception,
     *                   indicating the capability that caused the issue.
     */
    public CapabilityException(final String message, @Nullable final Throwable cause, final Capability capability) {
        super(message, cause);
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
