package net.thenextlvl.service.api.model;

import org.jspecify.annotations.NullMarked;

/**
 * Represents an object that can be persisted in a storage medium.
 * A Persistable object provides functionalities to check its persistent state,
 * retrieve its name, enable or disable persistence, and persist its current state.
 *
 * @since 2.2.0
 */
@NullMarked
public interface Persistable {
    /**
     * Retrieves the name of the object.
     *
     * @return the name of the object as a String.
     */
    String getName();

    /**
     * Determines whether the object is persistent.
     *
     * @return true if the object is persistent, false otherwise.
     */
    boolean isPersistent();

    /**
     * Persists the current state of the object.
     *
     * @return true if the object was successfully persisted, false otherwise.
     */
    boolean persist();

    /**
     * Sets the persistent state of the object.
     *
     * @param persistent a boolean value indicating whether the object should be marked as persistent.
     */
    void setPersistent(boolean persistent);
}
