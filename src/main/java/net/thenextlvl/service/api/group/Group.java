package net.thenextlvl.service.api.group;

import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.World;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * The Group interface represents a group with a display name, name, prefix, and suffix.
 */
public interface Group extends PermissionHolder {
    /**
     * Retrieves the display name associated with the group.
     *
     * @return An Optional containing the display name of the group.
     * Returns an empty Optional if no display name is set.
     */
    Optional<String> getDisplayName();

    /**
     * Retrieves the prefix associated with the group.
     *
     * @return An Optional containing the prefix of the group.
     * Returns an empty Optional if no prefix is set.
     */
    Optional<String> getPrefix();

    /**
     * Retrieves the suffix associated with the group.
     *
     * @return An Optional containing the suffix of the group.
     * Returns an empty Optional if no suffix is set.
     */
    Optional<String> getSuffix();

    /**
     * Retrieves the world associated with the group.
     *
     * @return An Optional containing the world of the group.
     * Returns an empty Optional if no world is set.
     */
    Optional<World> getWorld();

    /**
     * Retrieves the weight of the group.
     *
     * @return The weight of the group.
     */
    OptionalInt getWeight();

    /**
     * Returns the name of the group.
     *
     * @return The name of the group.
     */
    String getName();

    /**
     * Sets the display name of the group.
     *
     * @param displayName The display name to set for the group.
     * @return true if the display name was successfully set, false otherwise.
     */
    boolean setDisplayName(String displayName);

    /**
     * Sets the weight of the group.
     *
     * @param weight The weight to set for the group.
     * @return true if the weight was successfully set, false otherwise.
     */
    boolean setWeight(int weight);

    /**
     * Sets the prefix associated with the group.
     *
     * @param prefix The prefix to set for the group.
     * @return true if the prefix was successfully set, false otherwise.
     */
    default boolean setPrefix(String prefix) {
        return setPrefix(prefix, 0);
    }

    /**
     * Sets the prefix associated with the group.
     *
     * @param prefix   The prefix to set for the group.
     * @param priority The priority for the prefix. Higher values indicate higher precedence.
     * @return true if the prefix was successfully set, false otherwise.
     * @see Group#getPrefix()
     * @see Group#setPrefix(String)
     */
    boolean setPrefix(String prefix, int priority);

    /**
     * Sets the suffix associated with the group.
     *
     * @param suffix The suffix to set for the group.
     * @return true if the suffix was successfully set, false otherwise.
     */
    default boolean setSuffix(String suffix) {
        return setSuffix(suffix, 0);
    }

    /**
     * Sets the suffix associated with the group.
     *
     * @param suffix   The suffix to set for the group.
     * @param priority The priority for the suffix. Higher values indicate higher precedence.
     * @return true if the suffix was successfully set, false otherwise.
     * @see Group#getSuffix()
     * @see Group#setSuffix(String)
     */
    boolean setSuffix(String suffix, int priority);
}
