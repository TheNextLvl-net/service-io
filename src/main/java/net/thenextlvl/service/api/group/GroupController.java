package net.thenextlvl.service.api.group;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The GroupController interface provides methods for managing groups.
 */
public interface GroupController {
    /**
     * Retrieves the name associated with the permission controller.
     *
     * @return the name of the chat controller.
     */
    String getName();

    /**
     * Creates a new group with the given name.
     *
     * @param name the name of the group to create
     * @return a CompletableFuture that will complete with the created Group object
     */
    CompletableFuture<Group> createGroup(String name);

    /**
     * Creates a new group with the given name.
     *
     * @param name  the name of the group to create
     * @param world the world the group should be created for
     * @return a CompletableFuture that will complete with the created Group object
     */
    CompletableFuture<Group> createGroup(String name, World world);

    /**
     * Retrieves the group with the given name.
     *
     * @param name the name of the group to retrieve
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    CompletableFuture<Group> getGroup(String name);

    /**
     * Retrieves the group with the given name.
     *
     * @param name  the name of the group to retrieve
     * @param world the world the group should be received from
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    CompletableFuture<Group> getGroup(String name, World world);

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default CompletableFuture<GroupHolder> getGroupHolder(OfflinePlayer player) {
        return getGroupHolder(player.getUniqueId());
    }

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @param world  the world the group holder should be received from
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default CompletableFuture<GroupHolder> getGroupHolder(OfflinePlayer player, World world) {
        return getGroupHolder(player.getUniqueId(), world);
    }

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid the UUID of the player for which to retrieve the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    CompletableFuture<GroupHolder> getGroupHolder(UUID uuid);

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid  the UUID of the player for which to retrieve the GroupHolder
     * @param world the world the group holder should be received from
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    CompletableFuture<GroupHolder> getGroupHolder(UUID uuid, World world);

    /**
     * Retrieves all groups.
     *
     * @return A CompletableFuture that will complete with a collection of all the groups.
     */
    CompletableFuture<Set<Group>> getGroups();

    /**
     * Retrieves all groups.
     *
     * @param world the world the groups should be received from
     * @return A CompletableFuture that will complete with a collection of all the groups.
     */
    CompletableFuture<Set<Group>> getGroups(World world);

    /**
     * Deletes the given group.
     *
     * @param group the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Boolean> deleteGroup(Group group);

    /**
     * Deletes the given group.
     *
     * @param group the group to delete
     * @param world the world the group should be deleted from
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Boolean> deleteGroup(Group group, World world);

    /**
     * Deletes the group with the given name.
     *
     * @param name the name of the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Boolean> deleteGroup(String name);

    /**
     * Deletes the group with the given name.
     *
     * @param name  the name of the group to delete
     * @param world the world the group should be deleted from
     * @return a CompletableFuture that will complete when the group has been deleted
     */
    CompletableFuture<Boolean> deleteGroup(String name, World world);
}
