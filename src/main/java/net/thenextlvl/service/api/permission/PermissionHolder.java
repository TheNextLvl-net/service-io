package net.thenextlvl.service.api.permission;

import net.kyori.adventure.util.TriState;

/**
 * The {@code PermissionHolder} interface represents an entity that holds permissions.
 * It provides methods to check, add, and remove permissions for the holder.
 */
public interface PermissionHolder {
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
}
