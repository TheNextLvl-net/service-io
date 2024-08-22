package net.thenextlvl.service.api.group;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The GroupController interface provides methods for managing groups and the membership of players in groups.
 */
public interface GroupController {
    /**
     * Checks whether a given player is in the specified group.
     *
     * @param uniqueId The unique ID of the player.
     * @param group    The group to check membership for.
     * @return A CompletableFuture that will complete with a boolean indicating whether the player is in the group or not.
     */
    CompletableFuture<Boolean> inGroup(UUID uniqueId, Group group);

    /**
     * Checks whether a given player is in the specified group.
     *
     * @param uniqueId The unique ID of the player.
     * @param group    The name of the group to check membership for.
     * @return A CompletableFuture that will complete with a boolean indicating whether the player is in the group or not.
     */
    CompletableFuture<Boolean> inGroup(UUID uniqueId, String group);

    /**
     * Retrieves all groups.
     *
     * @return A CompletableFuture that will complete with a collection of all the groups.
     */
    CompletableFuture<Collection<Group>> getGroups();

    /**
     * Retrieves the groups associated with a player identified by a unique ID.
     *
     * @param uniqueId The unique ID of the player.
     * @return A CompletableFuture that will complete with a collection of Group objects associated with the player.
     */
    CompletableFuture<Collection<Group>> getGroups(UUID uniqueId);

    /**
     * Creates a new group with the given name.
     *
     * @param group the name of the group to create
     * @return a CompletableFuture that will complete with the created Group object
     */
    CompletableFuture<Group> createGroup(String group);

    /**
     * Retrieves the group with the given name.
     *
     * @param group the name of the group to retrieve
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    CompletableFuture<Group> getGroup(String group);

    /**
     * Retrieves the primary group of a player with the provided UUID.
     *
     * @param uniqueId The UUID of the player.
     * @return A CompletableFuture that will complete with the primary Group object of the player.
     */
    CompletableFuture<Group> getPrimaryGroup(UUID uniqueId);

    /**
     * Adds a group to the player with the provided unique ID.
     *
     * @param uniqueId the unique ID of the player to add the group to
     * @param group    the group to add
     * @return a CompletableFuture that will complete when the group has been successfully added to the player
     */
    CompletableFuture<Void> addGroup(UUID uniqueId, Group group);

    /**
     * Adds a group to a player with the provided unique ID.
     *
     * @param uniqueId the unique ID of the player to add the group to
     * @param group    the name of the group to add
     * @return a CompletableFuture that will complete when the group has been successfully added to the player
     */
    CompletableFuture<Void> addGroup(UUID uniqueId, String group);

    /**
     * Deletes the given group.
     *
     * @param group the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Void> deleteGroup(Group group);

    /**
     * Deletes the group with the given name.
     *
     * @param group the name of the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Void> deleteGroup(String group);

    /**
     * Removes the specified group from the player with the given unique ID.
     *
     * @param uniqueId the unique ID of the player
     * @param group    the group to be removed from the player
     * @return a CompletableFuture that will complete when the group has been successfully removed from the player
     */
    CompletableFuture<Void> removeGroup(UUID uniqueId, Group group);

    /**
     * Removes the specified group from the player with the given unique ID.
     *
     * @param uniqueId the unique ID of the player
     * @param group    the name of the group to be removed from the player
     * @return a CompletableFuture that will complete when the group has been successfully removed from the player
     */
    CompletableFuture<Void> removeGroup(UUID uniqueId, String group);
}
