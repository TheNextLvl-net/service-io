package net.thenextlvl.service.api.model;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * The InfoNode interface provides methods to retrieve, remove, and set information node values associated with keys.
 * An information node is a key-value pair where both key and value are stored as string but can be retrieved as any object.
 * The value is retrieved as an Optional, allowing for a null-safe operation.
 */
public interface InfoNode {
    /**
     * Retrieves the information node value associated with the given key.
     *
     * @param key    the key of the information node to retrieve the value for
     * @param mapper the function used to map the string value to the desired type
     * @param <T>    the type of the value to retrieve
     * @return an {@code Optional<T>} containing the information node value if it exists
     */
    <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper);

    /**
     * Removes the information node with the specified key from the object.
     *
     * @param key the key of the information node to be removed
     * @return true if the information node was successfully removed, false otherwise
     */
    boolean removeInfoNode(String key);

    /**
     * Removes the information node with the specified key and value from the object.
     *
     * @param key   the key of the information node to be removed
     * @param value the value of the information node to be removed
     * @return true if the information node was successfully removed, false otherwise
     */
    default boolean removeInfoNode(String key, String value) {
        var infoNode = getInfoNode(key).orElse(null);
        if (!value.equals(infoNode)) return false;
        return removeInfoNode(key);
    }

    /**
     * Sets the information node value for the specified key.
     *
     * @param key   the key to set the value for
     * @param value the value to set
     * @return true if the information node value was set successfully, false otherwise
     */
    boolean setInfoNode(String key, String value);

    /**
     * Retrieves the boolean information node value associated with the given key.
     *
     * @param key the key to retrieve the value for
     * @return an {@code Optional<Boolean>} containing the information node value if it exists
     */
    default Optional<Boolean> booleanInfoNode(String key) {
        return getInfoNode(key, Boolean::parseBoolean);
    }

    /**
     * Retrieves the information node value associated with the given key.
     *
     * @param key the key to retrieve the value for
     * @return an {@code Optional<String>} containing the information node value if it exists
     */
    default Optional<String> getInfoNode(String key) {
        return getInfoNode(key, Function.identity());
    }

    /**
     * Retrieves the information node value associated with the given key as a {@code OptionalDouble}.
     *
     * @param key the key to retrieve the value for
     * @return an {@code OptionalDouble} containing the information node value if it exists
     * @throws NumberFormatException if the value associated with the key cannot be parsed into a double
     */
    default Optional<Double> doubleInfoNode(String key) throws NumberFormatException {
        return getInfoNode(key, s -> s != null ? Double.parseDouble(s) : null);
    }

    /**
     * Retrieves the information node value associated with the given key as an {@code OptionalInt}.
     *
     * @param key the key to retrieve the value for
     * @return an {@code OptionalInt} containing the information node value if it exists
     * @throws NumberFormatException if the value associated with the key cannot be parsed into an integer
     */
    default Optional<Integer> intInfoNode(String key) throws NumberFormatException {
        return getInfoNode(key, s -> s != null ? Integer.parseInt(s) : null);
    }

    /**
     * Checks if the given key exists in the information nodes of the object.
     *
     * @param key the key to check for existence
     * @return true if the key exists in the information nodes, false otherwise
     */
    default boolean hasInfoNode(String key) {
        return getInfoNode(key).isPresent();
    }
}
