package net.thenextlvl.service.api.model;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

/**
 * The Positioned interface represents an object with a specific position and orientation within a world.
 * It provides methods to retrieve coordinates, rotation, and associated world and server information.
 */
@NullMarked
public interface Positioned {
    /**
     * Retrieves the location of the object.
     *
     * @return the {@link Location} of the object.
     */
    Location getLocation();

    /**
     * Retrieves the current server instance.
     *
     * @return the current {@link Server} instance.
     */
    Server getServer();

    /**
     * Retrieves the world associated with the object.
     *
     * @return the {@link World} in which the object is located.
     */
    World getWorld();

    /**
     * Retrieves the X coordinate of the object's position.
     *
     * @return the X coordinate as a double.
     */
    double getX();

    /**
     * Retrieves the Y coordinate of the object's position.
     *
     * @return the Y coordinate as a double.
     */
    double getY();

    /**
     * Retrieves the Z coordinate of the object's position.
     *
     * @return the Z coordinate as a double.
     */
    double getZ();

    /**
     * Retrieves the pitch of the object's position.
     *
     * @return the pitch, which represents the vertical rotation of the object.
     */
    float getPitch();

    /**
     * Retrieves the yaw of the object's position.
     *
     * @return the yaw, representing the horizontal rotation of the object.
     */
    float getYaw();
}
