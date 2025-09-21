package net.thenextlvl.service.api.group;

import net.thenextlvl.service.api.model.Display;
import net.thenextlvl.service.api.permission.PermissionHolder;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * The Group interface represents a group entity that holds permissions and display attributes such as
 * a display name, prefix, and suffix. It provides methods to manage these attributes as well as the
 * group weight and associated world.
 *
 * @since 1.0.0
 */
@NullMarked
public interface Group extends PermissionHolder, Display {
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
     * Returns the name of the object.
     *
     * @return The name of the object.
     */
    String getName();

    /**
     * Sets the weight of the group.
     *
     * @param weight The weight to set for the group.
     * @return true if the weight was successfully set, false otherwise.
     */
    boolean setWeight(int weight);
}
