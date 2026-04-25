package net.thenextlvl.service.group;

import net.thenextlvl.service.Controller;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The GroupController interface provides methods for managing groups and group holders.
 * It allows creating, loading, deleting, and retrieving groups and group holders.
 * Operations can be performed asynchronously using CompletableFutures.
 *
 * @implSpec Implementations must be thread-safe. All methods may be called from any thread,
 * including the main server thread and asynchronous task threads concurrently.
 * @since 1.0.0
 */
public interface GroupController extends Controller {
    /**
     * Creates a new group with the given name.
     *
     * @param name the name of the group to create
     * @return a CompletableFuture that will complete with the created Group object
     * @since 3.0.0
     */
    CompletableFuture<Group> createGroup(String name);

    /**
     * Creates a new group with the given name.
     *
     * @param name  the name of the group to create
     * @param world the world the group should be created for
     * @return a CompletableFuture that will complete with the created Group object
     * @since 3.0.0
     */
    CompletableFuture<Group> createGroup(String name, World world);

    /**
     * Loads the group with the given name asynchronously.
     *
     * @param name the name of the group to load
     * @return a CompletableFuture that will complete with the loaded Group object
     * @since 3.0.0
     */
    CompletableFuture<Group> loadGroup(String name);

    /**
     * Loads a group asynchronously by its name and world.
     *
     * @param name  the name of the group to load
     * @param world the world the group should be loaded from
     * @return a CompletableFuture that will complete with the loaded Group object
     * @since 3.0.0
     */
    CompletableFuture<Group> loadGroup(String name, World world);

    /**
     * Loads the GroupHolder asynchronously associated with the specified player.
     *
     * @param player the player for whom to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     * @since 3.0.0
     */
    CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player);

    /**
     * Loads the GroupHolder asynchronously associated with the specified player and world.
     *
     * @param player the player for whom to load the GroupHolder
     * @param world  the world from which to load the GroupHolder
     * @return a CompletableFuture that will complete with the loaded GroupHolder object
     * @since 3.0.0
     */
    CompletableFuture<GroupHolder> loadGroupHolder(OfflinePlayer player, World world);

    /**
     * Loads the GroupHolder asynchronously associated with the specified player's UUID.
     *
     * @param uuid the UUID of the player for whom to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> loadGroupHolder(final UUID uuid) {
        return loadGroupHolder(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Loads the GroupHolder asynchronously associated with the specified player's UUID and world.
     *
     * @param uuid  the UUID of the player for whom to load the GroupHolder
     * @param world the world from which to load the GroupHolder
     * @return a CompletableFuture that will complete with the retrieved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> loadGroupHolder(final UUID uuid, final World world) {
        return loadGroupHolder(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Resolves the group with the given name, loading from the backing store if not cached.
     *
     * @param name the name of the group to resolve
     * @return a CompletableFuture that will complete with the resolved Group object
     * @since 3.0.0
     */
    default CompletableFuture<Group> resolveGroup(final String name) {
        return getGroup(name)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroup(name));
    }

    /**
     * Resolves the group with the given name and world, loading from the backing store if not cached.
     *
     * @param name  the name of the group to resolve
     * @param world the world the group should be resolved from
     * @return a CompletableFuture that will complete with the resolved Group object
     * @since 3.0.0
     */
    default CompletableFuture<Group> resolveGroup(final String name, final World world) {
        return getGroup(name, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroup(name, world));
    }

    /**
     * Resolves the GroupHolder associated with the specified player, loading if not cached.
     *
     * @param player the player for whom to resolve the GroupHolder
     * @return a CompletableFuture that will complete with the resolved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> resolveGroupHolder(final OfflinePlayer player) {
        return getGroupHolder(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(player));
    }

    /**
     * Resolves the GroupHolder associated with the specified player and world, loading if not cached.
     *
     * @param player the player for whom to resolve the GroupHolder
     * @param world  the world from which to resolve the GroupHolder
     * @return a CompletableFuture that will complete with the resolved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> resolveGroupHolder(final OfflinePlayer player, final World world) {
        return getGroupHolder(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(player, world));
    }

    /**
     * Resolves the GroupHolder associated with the specified player's UUID, loading if not cached.
     *
     * @param uuid the UUID of the player for whom to resolve the GroupHolder
     * @return a CompletableFuture that will complete with the resolved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> resolveGroupHolder(final UUID uuid) {
        return getGroupHolder(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(uuid));
    }

    /**
     * Resolves the GroupHolder associated with the specified player's UUID and world, loading if not cached.
     *
     * @param uuid  the UUID of the player for whom to resolve the GroupHolder
     * @param world the world from which to resolve the GroupHolder
     * @return a CompletableFuture that will complete with the resolved GroupHolder object
     * @since 3.0.0
     */
    default CompletableFuture<GroupHolder> resolveGroupHolder(final UUID uuid, final World world) {
        return getGroupHolder(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> loadGroupHolder(uuid, world));
    }

    /**
     * Loads all groups from the backing store.
     *
     * @return a CompletableFuture that will complete with a set of groups
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Group>> loadGroups();

    /**
     * Loads all groups from the backing store for the given world.
     *
     * @param world the world for which to load the groups
     * @return a CompletableFuture that will complete with a set of groups
     * @since 3.0.0
     */
    CompletableFuture<@Unmodifiable Set<Group>> loadGroups(World world);

    /**
     * Deletes the given group.
     *
     * @param group the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     * @since 3.0.0
     */
    default CompletableFuture<Boolean> deleteGroup(final Group group) {
        return group.getWorld()
                .map(world -> deleteGroup(group.getName(), world))
                .orElseGet(() -> deleteGroup(group.getName()));
    }

    /**
     * Deletes the group with the given name.
     *
     * @param name the name of the group to delete
     * @return a CompletableFuture that will complete when the group has been deleted
     * @since 3.0.0
     */
    CompletableFuture<Boolean> deleteGroup(String name);

    /**
     * Deletes the group with the given name.
     *
     * @param name  the name of the group to delete
     * @param world the world the group should be deleted from
     * @return a CompletableFuture that will complete when the group has been deleted
     * @since 3.0.0
     */
    CompletableFuture<Boolean> deleteGroup(String name, World world);

    /**
     * Retrieves the group with the given name.
     *
     * @param name the name of the group to retrieve
     * @return an optional containing the Group, or empty
     * @since 3.0.0
     */
    Optional<Group> getGroup(String name);

    /**
     * Retrieves the group with the given name.
     *
     * @param name  the name of the group to retrieve
     * @param world the world the group should be received from
     * @return an optional containing the Group, or empty
     * @since 3.0.0
     */
    Optional<Group> getGroup(String name, World world);

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @return an optional containing the GroupHolder, or empty
     * @since 3.0.0
     */
    Optional<GroupHolder> getGroupHolder(OfflinePlayer player);

    /**
     * Retrieves the GroupHolder associated with the given player.
     *
     * @param player the player for which to retrieve the GroupHolder
     * @param world  the world the group holder should be received from
     * @return an optional containing the GroupHolder, or empty
     * @since 3.0.0
     */
    Optional<GroupHolder> getGroupHolder(OfflinePlayer player, World world);

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid the UUID of the player for which to retrieve the GroupHolder
     * @return an optional containing the GroupHolder, or empty
     * @since 3.0.0
     */
    default Optional<GroupHolder> getGroupHolder(final UUID uuid) {
        return getGroupHolder(getPlugin().getServer().getOfflinePlayer(uuid));
    }

    /**
     * Retrieves the GroupHolder associated with the given UUID.
     *
     * @param uuid  the UUID of the player for which to retrieve the GroupHolder
     * @param world the world the group holder should be received from
     * @return an optional containing the GroupHolder, or empty
     * @since 3.0.0
     */
    default Optional<GroupHolder> getGroupHolder(final UUID uuid, final World world) {
        return getGroupHolder(getPlugin().getServer().getOfflinePlayer(uuid), world);
    }

    /**
     * Retrieves all groups.
     *
     * @return a set of all the groups.
     * @since 3.0.0
     */
    @Unmodifiable
    Set<Group> getGroups();

    /**
     * Retrieves all groups in the specified world.
     *
     * @param world the world the groups should be received from
     * @return a set of all the groups in the specified world.
     * @since 3.0.0
     */
    @Unmodifiable
    Set<Group> getGroups(World world);
}
