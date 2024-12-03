package net.thenextlvl.service.api.group;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The GroupController interface provides methods for managing groups and group holders.
 * It allows creating, loading, deleting, and retrieving groups and group holders.
 * Operations can be performed asynchronously using CompletableFutures.
 */
@NullMarked
public interface GroupController {
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
     * Retrieves the group with the given name asynchronously.
     *
     * @param name the name of the group to retrieve
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    CompletableFuture<Group> loadGroup(String name);

    /**
     * Loads a group asynchronously by its name and world.
     *
     * @param name  the name of the group to load
     * @param world the world the group should be loaded from
     * @return a CompletableFuture that will complete with the loaded Group object
     */
    CompletableFuture<Group> loadGroup(String name, World world);

    /**
     * Loads the GroupHolder asynchronously associated with the specified player.
     *
     * @param player the player for whom to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player) {
        return loadGroupHolder(player.getUniqueId());
    }

    /**
     * Retrieves the GroupHolder asynchronously associated with the specified player and world.
     *
     * @param player the player for whom to load the GroupHolder
     * @param world  the world from which to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player, World world) {
        return loadGroupHolder(player.getUniqueId(), world);
    }

    /**
     * Loads the GroupHolder asynchronously associated with the specified player's UUID.
     *
     * @param uuid the UUID of the player for whom to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid);

    /**
     * Loads the GroupHolder asynchronously associated with the specified player's UUID and world.
     *
     * @param uuid  the UUID of the player for whom to load the GroupHolder
     * @param world the world from which to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    CompletableFuture<GroupHolder> loadGroupHolder(UUID uuid, World world);

    default CompletableFuture<Group> tryGetGroup(String name) {
        return getGroup(name)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroup(name));
    }

    default CompletableFuture<Group> tryGetGroup(String name, World world) {
        return getGroup(name, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroup(name, world));
    }

    default CompletableFuture<GroupHolder> tryGetGroupHolder(OfflinePlayer player) {
        return getGroupHolder(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(player));
    }

    default CompletableFuture<GroupHolder> tryGetGroupHolder(OfflinePlayer player, World world) {
        return getGroupHolder(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(player, world));
    }

    default CompletableFuture<GroupHolder> tryGetGroupHolder(UUID uuid) {
        return getGroupHolder(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(uuid));
    }

    default CompletableFuture<GroupHolder> tryGetGroupHolder(UUID uuid, World world) {
        return getGroupHolder(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(uuid, world));
    }

    /**
     * Retrieves a set of groups asynchronously.
     *
     * @return a CompletableFuture that will complete with a set of groups
     */
    CompletableFuture<@Unmodifiable Set<Group>> loadGroups();

    /**
     * Retrieves a set of groups asynchronously for the given world.
     *
     * @param world the world for which to retrieve the groups
     * @return a CompletableFuture that will complete with a set of groups
     */
    CompletableFuture<@Unmodifiable Set<Group>> loadGroups(World world);

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

    /**
     * Retrieves the group with the given name.
     *
     * @param name the name of the group to retrieve
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    Optional<Group> getGroup(String name);

    /**
     * Retrieves the group with the given name.
     *
     * @param name  the name of the group to retrieve
     * @param world the world the group should be received from
     * @return a CompletableFuture that will complete with the retrieved Group object
     */
    Optional<Group> getGroup(String name, World world);

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default Optional<GroupHolder> getGroupHolder(OfflinePlayer player) {
        return getGroupHolder(player.getUniqueId());
    }

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @param world  the world the group holder should be received from
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    default Optional<GroupHolder> getGroupHolder(OfflinePlayer player, World world) {
        return getGroupHolder(player.getUniqueId(), world);
    }

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid the UUID of the player for which to retrieve the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    Optional<GroupHolder> getGroupHolder(UUID uuid);

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid  the UUID of the player for which to retrieve the GroupHolder
     * @param world the world the group holder should be received from
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     */
    Optional<GroupHolder> getGroupHolder(UUID uuid, World world);

    /**
     * Retrieves all groups.
     *
     * @return A CompletableFuture that will complete with a collection of all the groups.
     */
    @Unmodifiable
    Set<Group> getGroups();

    /**
     * Retrieves all groups.
     *
     * @param world the world the groups should be received from
     * @return A CompletableFuture that will complete with a collection of all the groups.
     */
    @Unmodifiable
    Set<Group> getGroups(World world);

    /**
     * Retrieves the name associated with the permission controller.
     *
     * @return the name of the chat controller.
     */
    @Contract(pure = true)
    String getName();
}
