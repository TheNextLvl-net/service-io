package net.thenextlvl.service.group;

import net.thenextlvl.service.model.Display;
import net.thenextlvl.service.permission.PermissionHolder;
import org.bukkit.World;

import java.util.Optional;

/**
 * The Group interface represents a group entity that holds permissions and display attributes such as
 * a display name, prefix, and suffix. It provides methods to manage these attributes as well as the
 * group weight and associated world.
 *
 * @since 1.0.0
 */
public interface Group extends PermissionHolder, Display {
    /**
     * Retrieves the world associated with the group.
     *
     * @return An Optional containing the world of the group.
     * Returns an empty Optional if no world is set.
     * @since 1.0.0
     */
    Optional<World> getWorld();

    /**
     * Retrieves the weight of the group.
     *
     * @return The weight of the group.
     * @since 3.0.0
     */
    Optional<Integer> getWeight();

    /**
     * Returns the name of the object.
     *
     * @return The name of the object.
     * @since 1.0.0
     */
    String getName();

    /**
     * Sets the weight of the group.
     *
     * @param weight The weight to set for the group.
     * @return true if the weight was successfully set, false otherwise.
     * @since 1.0.0
     */
    boolean setWeight(int weight);
}
