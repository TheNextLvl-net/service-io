package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.group.Group;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * The ChatProfile interface represents a chat profile for a user.
 */
public interface ChatProfile {
    /**
     * Retrieves the name associated with the chat profile.
     *
     * @return An Optional containing the name of the chat profile.
     * Returns an empty Optional if no name is set.
     */
    Optional<String> getName();

    /**
     * Retrieves the prefix associated with the chat profile.
     *
     * @return An Optional containing the prefix of the chat profile.
     * Returns an empty Optional if no prefix is set.
     */
    Optional<String> getPrefix();

    /**
     * Retrieves the primary group associated with the chat profile.
     *
     * @return An Optional containing the primary group of the chat profile.
     * Returns an empty Optional if no primary group is set.
     */
    Optional<String> getPrimaryGroup();

    /**
     * Retrieves the suffix associated with the chat profile.
     *
     * @return An Optional containing the suffix of the chat profile.
     * Returns an empty Optional if no suffix is set.
     */
    Optional<String> getSuffix();

    /**
     * Retrieves the groups associated with the chat profile.
     *
     * @return The groups associated with the chat profile.
     */
    @Unmodifiable
    Set<Group> getGroups();

    /**
     * Sets the primary group for the chat profile.
     *
     * @param group The name of the group to be set as the primary group.
     * @return True if the primary group was successfully set, false otherwise.
     */
    boolean setPrimaryGroup(@NotNull String group);

    /**
     * Sets the prefix associated with the chat profile.
     *
     * @param prefix The prefix to set for the chat profile.
     */
    default void setPrefix(String prefix) {
        setPrefix(prefix, 0);
    }

    /**
     * Sets the prefix associated with the chat profile.
     *
     * @param prefix   The prefix to set for the chat profile.
     * @param priority The priority for the prefix. Higher values indicate higher precedence.
     * @see ChatProfile#getPrefix()
     * @see ChatProfile#setPrefix(String)
     */
    void setPrefix(String prefix, int priority);

    /**
     * Sets the suffix associated with the chat profile.
     *
     * @param suffix The suffix to set for the chat profile.
     */
    default void setSuffix(String suffix) {
        setSuffix(suffix, 0);
    }

    /**
     * Sets the suffix associated with the chat profile.
     *
     * @param suffix   The suffix to set for the chat profile.
     * @param priority The priority for the suffix. Higher values indicate higher precedence.
     * @see ChatProfile#getSuffix()
     * @see ChatProfile#setSuffix(String)
     */
    void setSuffix(String suffix, int priority);
}
