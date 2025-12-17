package net.thenextlvl.service.api.group;

import net.thenextlvl.service.api.permission.PermissionHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * The {@code GroupHolder} interface represents an entity that holds groups.
 * It extends the {@link PermissionHolder} interface.
 * It provides methods to retrieve and manipulate groups for the holder.
 *
 * @since 1.0.0
 */
@NullMarked
public interface GroupHolder extends PermissionHolder {
    /**
     * Retrieves the groups associated with the permission holder.
     *
     * @return A set of Group objects representing the groups associated with the permission holder.
     */
    @Unmodifiable
    Set<Group> getGroups();

    /**
     * Retrieves the primary group of the permission holder.
     * The primary group represents the main group assigned to the permission holder.
     *
     * @return The primary group of the permission holder as a string.
     * @see GroupHolder#getGroups()
     * @see GroupHolder#setPrimaryGroup(Group)
     * @see GroupHolder#setPrimaryGroup(String)
     */
    String getPrimaryGroup();

    /**
     * Adds a group to the permission holder.
     *
     * @param group the group object to be added
     * @return true if the group was successfully added, false otherwise
     */
    boolean addGroup(Group group);

    /**
     * Adds a group to the permission holder.
     *
     * @param name the name of the group to be added
     * @return true if the group was successfully added, false otherwise
     */
    boolean addGroup(String name);

    /**
     * Checks if the permission holder is in the specified group.
     *
     * @param group the group to check
     * @return true if the permission holder is in the group, false otherwise
     */
    boolean inGroup(Group group);

    /**
     * Checks if the permission holder is in the specified group.
     *
     * @param name the name of the group to check
     * @return true if the permission holder is in the group, false otherwise
     */
    boolean inGroup(String name);

    /**
     * Removes a group from the permission holder.
     *
     * @param group the group to be removed
     * @return true if the group was successfully removed, false otherwise
     */
    boolean removeGroup(Group group);

    /**
     * Removes a group from the permission holder.
     *
     * @param name the name of the group to be removed
     * @return true if the group was successfully removed, false otherwise
     */
    boolean removeGroup(String name);

    /**
     * Sets the primary group for the permission holder.
     *
     * @param group the group to set as the primary group
     * @return true if the primary group is successfully set, false otherwise
     */
    boolean setPrimaryGroup(Group group);

    /**
     * Sets the primary group for the permission holder.
     *
     * @param name the name of the group to set as the primary group
     * @return true if the primary group is successfully set, false otherwise
     */
    boolean setPrimaryGroup(String name);
}
