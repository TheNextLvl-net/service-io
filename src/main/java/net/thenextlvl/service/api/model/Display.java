package net.thenextlvl.service.api.model;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * The Display interface provides methods to manage display names, prefixes, and suffixes associated with an object.
 */
@NullMarked
public interface Display {
    /**
     * Retrieves the display name associated with the object.
     *
     * @return An Optional containing the display name of the object.
     * Returns an empty Optional if no display name is set.
     */
    Optional<String> getDisplayName();

    /**
     * Retrieves the prefix associated with the object.
     *
     * @return An Optional containing the prefix of the object.
     * Returns an empty Optional if no prefix is set.
     */
    default Optional<String> getPrefix() {
        return getPrefix(0);
    }

    /**
     * Retrieves the prefix associated with the object based on the given priority.
     *
     * @param priority The priority for the prefix. Higher values indicate higher precedence.
     * @return An Optional containing the prefix of the object. Returns an empty Optional if no prefix is set for the given priority.
     */
    Optional<String> getPrefix(int priority);

    /**
     * Retrieves the prefixes associated with the object, organized by their priorities.
     *
     * @return A map where the keys are priorities and the values are the corresponding prefixes.
     */
    @Unmodifiable
    Map<Integer, String> getPrefixes();

    /**
     * Retrieves the suffix associated with the object.
     *
     * @return An Optional containing the suffix of the object.
     * Returns an empty Optional if no suffix is set.
     */
    default Optional<String> getSuffix() {
        return getSuffix(0);
    }

    /**
     * Retrieves the suffix associated with the object based on the given priority.
     *
     * @param priority The priority for the suffix. Higher values indicate higher precedence.
     * @return An Optional containing the suffix of the object. Returns an empty Optional if no suffix is set for the given priority.
     */
    Optional<String> getSuffix(int priority);

    /**
     * Retrieves the suffixes associated with the object, organized by their priorities.
     *
     * @return A map where the keys are priorities and the values are the corresponding suffixes.
     */
    @Unmodifiable
    Map<Integer, String> getSuffixes();

    /**
     * Sets the display name of the object.
     *
     * @param displayName The display name to set for the object.
     * @return true if the display name was successfully set, false otherwise.
     */
    boolean setDisplayName(@Nullable String displayName);

    /**
     * Sets the prefix associated with the object.
     *
     * @param prefix The prefix to set for the object.
     * @return true if the prefix was successfully set, false otherwise.
     */
    default boolean setPrefix(@Nullable String prefix) {
        return setPrefix(prefix, 0);
    }

    /**
     * Sets the prefix associated with the object.
     *
     * @param prefix   The prefix to set for the object.
     * @param priority The priority for the prefix. Higher values indicate higher precedence.
     * @return true if the prefix was successfully set, false otherwise.
     * @see Display#getPrefix()
     * @see Display#setPrefix(String)
     */
    boolean setPrefix(@Nullable String prefix, int priority);

    /**
     * Sets the suffix associated with the object.
     *
     * @param suffix The suffix to set for the object.
     * @return true if the suffix was successfully set, false otherwise.
     */
    default boolean setSuffix(@Nullable String suffix) {
        return setSuffix(suffix, 0);
    }

    /**
     * Sets the suffix associated with the object.
     *
     * @param suffix   The suffix to set for the object.
     * @param priority The priority for the suffix. Higher values indicate higher precedence.
     * @return true if the suffix was successfully set, false otherwise.
     * @see Display#getSuffix()
     * @see Display#setSuffix(String)
     */
    boolean setSuffix(@Nullable String suffix, int priority);
}
