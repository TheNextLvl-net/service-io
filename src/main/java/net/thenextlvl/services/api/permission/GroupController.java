package net.thenextlvl.services.api.permission;

import java.util.concurrent.CompletableFuture;

/**
 * The GroupController interface provides methods to create, retrieve, and delete groups.
 */
public interface GroupController {
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
     * Deletes the group with the given name.
     *
     * @param group the name of the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Void> deleteGroup(String group);
}
