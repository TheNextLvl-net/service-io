package net.thenextlvl.service.api.permission;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.node.InfoNode;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * The {@code PermissionHolder} interface represents an entity that holds permissions.
 * It extends the {@link InfoNode} interface.
 * It provides methods to check, add, and remove permissions for the holder.
 */
public interface PermissionHolder extends InfoNode {
    /**
     * Retrieves the permissions held by the permission holder.
     *
     * @return a map where the keys are permission names and the values are booleans
     * indicating whether the permission is granted (true) or revoked (false)
     */
    @Unmodifiable
    Map<String, Boolean> getPermissions();

    /**
     * Checks if the specified permission is granted.
     *
     * @param permission the permission to check
     * @return a TriState value indicating the permission status (true, false, undefined)
     */
    TriState checkPermission(String permission);

    /**
     * Adds a permission to the permission holder.
     *
     * @param permission the permission to be added
     * @return true if the permission was successfully added, false otherwise
     */
    boolean addPermission(String permission);

    /**
     * Removes a permission from the permission holder.
     *
     * @param permission the permission to be removed
     * @return true if the permission was successfully removed, false otherwise
     */
    boolean removePermission(String permission);

    /**
     * Sets the specified permission for the permission holder.
     *
     * @param permission the name of the permission to set
     * @param value      a boolean indicating whether the permission is granted (true) or revoked (false)
     * @return true if the permission was successfully set, false otherwise
     */
    boolean setPermission(String permission, boolean value);
}
