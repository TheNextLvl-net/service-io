package net.thenextlvl.services.api.chat;

import net.thenextlvl.services.api.permission.Group;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The ChatProfile interface represents a chat profile for a user.
 */
public interface ChatProfile {
    /**
     * Retrieves the primary group of the chat profile.
     *
     * @return The primary group of the chat profile.
     */
    @Nullable
    Group getPrimaryGroup();

    /**
     * Retrieves the groups associated with the chat profile.
     *
     * @return The groups associated with the chat profile.
     */
    Set<Group> getGroups();

    /**
     * Retrieves the name associated with the chat profile.
     *
     * @return The name of the chat profile as a string.
     */
    @Nullable
    String getName();

    /**
     * Retrieves the prefix for the chat profile.
     *
     * @return The prefix as a string.
     */
    @Nullable
    String getPrefix();

    /**
     * Retrieves the suffix for the chat profile.
     *
     * @return The suffix as a string.
     */
    @Nullable
    String getSuffix();

    /**
     * Sets the prefix for the chat profile.
     *
     * @param prefix The prefix to be set as a string.
     */
    void setPrefix(@Nullable String prefix);

    /**
     * Sets the suffix for the chat profile.
     *
     * @param suffix The suffix to be set as a string.
     */
    void setSuffix(@Nullable String suffix);
}
