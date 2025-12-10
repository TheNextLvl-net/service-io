package net.thenextlvl.service.api.capability;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;

/**
 * A generic interface providing methods to query and check support for specific capabilities.
 * <p>
 * The {@code CapabilityProvider} interface allows implementing classes to define a set of supported
 * capabilities and provides mechanisms to check whether the provider supports individual or multiple capabilities.
 *
 * @param <T> the type of {@link Capability} supported by this provider
 * @since 2.2.0
 */
@NullMarked
public interface CapabilityProvider<T extends Capability> {
    /**
     * Retrieves an unmodifiable set of all available capabilities supported by the capability provider.
     *
     * @return an unmodifiable {@link Set} containing the supported {@link T capability} values
     */
    @Unmodifiable
    Set<T> getCapabilities();

    /**
     * Checks whether all the specified capabilities are supported by the capability provider.
     *
     * @param capabilities the collection of {@link T capability} instances to verify for support
     * @return {@code true} if all the specified capabilities are supported; {@code false} otherwise
     */
    boolean hasCapabilities(Collection<T> capabilities);

    /**
     * Checks whether the specified capability is supported.
     *
     * @param capability the {@link T capability} to verify for support
     * @return {@code true} if the given capability is supported; {@code false} otherwise
     */
    boolean hasCapability(T capability);
}
