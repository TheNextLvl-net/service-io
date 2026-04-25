package net.thenextlvl.service.model;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Optional provider-backed metadata support for holders, groups, and profiles.
 * <p>
 * This interface is intentionally separate from the core service contracts.
 * Only implementations backed by a real metadata concept should implement it.
 * Examples include provider meta values, variables, or chat info nodes.
 * Providers without native metadata support should simply not implement this
 * interface instead of returning fake defaults or no-op write results.
 * <p>
 * Metadata is modeled as string-keyed data with string storage semantics.
 * The typed accessors on this interface are convenience methods layered on top
 * of the raw string lookup and parsing behavior.
 * <p>
 * Callers should treat this interface as optional capability:
 * use {@code instanceof MetadataHolder} or another explicit support check
 * before reading or mutating metadata.
 */
public interface MetadataHolder {
    /**
     * Resolves metadata for the given key and maps the stored string value to a
     * caller-defined type.
     *
     * @param key    the metadata key
     * @param mapper mapping function applied to the stored value
     * @param <T>    mapped result type
     * @return the mapped value when metadata exists for the key
     */
    <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper);

    /**
     * Removes metadata for the given key.
     *
     * @param key the metadata key
     * @return {@code true} when the provider accepted the removal request
     */
    boolean removeInfoNode(String key);

    /**
     * Removes metadata only when the stored value matches the supplied value.
     *
     * @param key   the metadata key
     * @param value the expected stored value
     * @return {@code true} when the key existed with the expected value and was removed
     */
    default boolean removeInfoNode(final String key, final String value) {
        final var infoNode = getInfoNode(key).orElse(null);
        if (!value.equals(infoNode)) return false;
        return removeInfoNode(key);
    }

    /**
     * Stores metadata for the given key.
     *
     * @param key   the metadata key
     * @param value the metadata value to store
     * @return {@code true} when the provider accepted the update
     */
    boolean setInfoNode(String key, String value);

    /**
     * Reads metadata as a boolean.
     *
     * @param key the metadata key
     * @return the parsed boolean value when present
     */
    default Optional<Boolean> booleanInfoNode(final String key) {
        return getInfoNode(key, s -> s != null ? Boolean.parseBoolean(s) : null);
    }

    /**
     * Reads metadata as a raw string.
     *
     * @param key the metadata key
     * @return the stored string value when present
     */
    default Optional<String> getInfoNode(final String key) {
        return getInfoNode(key, Function.identity());
    }

    /**
     * Reads metadata as a double.
     *
     * @param key the metadata key
     * @return the parsed double value when present
     * @throws NumberFormatException when the stored value cannot be parsed as a double
     */
    default Optional<Double> doubleInfoNode(final String key) throws NumberFormatException {
        return getInfoNode(key, s -> s != null ? Double.parseDouble(s) : null);
    }

    /**
     * Reads metadata as an integer.
     *
     * @param key the metadata key
     * @return the parsed integer value when present
     * @throws NumberFormatException when the stored value cannot be parsed as an integer
     */
    default Optional<Integer> intInfoNode(final String key) throws NumberFormatException {
        return getInfoNode(key, s -> s != null ? Integer.parseInt(s) : null);
    }

    /**
     * Checks whether metadata exists for the given key.
     *
     * @param key the metadata key
     * @return {@code true} when metadata is present
     */
    default boolean hasInfoNode(final String key) {
        return getInfoNode(key).isPresent();
    }
}
