package net.thenextlvl.services.chat;

import net.thenextlvl.services.permission.Group;

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
    Group getPrimaryGroup();

    /**
     * Retrieves the groups associated with the chat profile.
     *
     * @return The groups associated with the chat profile.
     */
    Set<Group> getGroups();

    /**
     * Retrieves the prefix for the chat profile.
     *
     * @return The prefix as a string.
     */
    String getPrefix();

    /**
     * Retrieves the suffix for the chat profile.
     *
     * @return The suffix as a string.
     */
    String getSuffix();

    /**
     * Sets the prefix for the chat profile.
     *
     * @param prefix The prefix to be set as a string.
     */
    void setPrefix(String prefix);

    /**
     * Sets the suffix for the chat profile.
     *
     * @param suffix The suffix to be set as a string.
     */
    void setSuffix(String suffix);
}
