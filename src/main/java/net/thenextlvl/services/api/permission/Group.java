package net.thenextlvl.services.api.permission;

import org.jetbrains.annotations.Nullable;

/**
 * The Group interface represents a group with a display name, name, prefix, and suffix.
 */
public interface Group extends PermissionHolder {
    /**
     * Returns the display name of the group.
     *
     * @return The display name of the group, or null if it has no display name.
     */
    @Nullable
    String getDisplayName();

    /**
     * Returns the name of the group.
     *
     * @return The name of the group.
     */
    String getName();

    /**
     * Retrieves the prefix associated with the group.
     *
     * @return The prefix of the group, or null if it has no prefix.
     */
    @Nullable
    String getPrefix();

    /**
     * Retrieves the suffix associated with the group.
     *
     * @return The suffix of the group, or null if it has no suffix.
     */
    @Nullable
    String getSuffix();

    /**
     * Sets the display name of the group.
     *
     * @param displayName The display name to set for the group.
     */
    void setDisplayName(@Nullable String displayName);

    /**
     * Sets the name of the group.
     *
     * @param name The name to set for the group.
     */
    void setName(String name);

    /**
     * Sets the prefix associated with the group.
     *
     * @param prefix The prefix to set for the group.
     */
    void setPrefix(@Nullable String prefix);

    /**
     * Sets the suffix associated with the group.
     *
     * @param suffix The suffix to set for the group.
     */
    void setSuffix(@Nullable String suffix);
}
