package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.node.InfoNode;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

/**
 * The ChatProfile interface represents a chat profile for a user.
 */
public interface ChatProfile extends InfoNode {
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
     * Retrieves the name of the groups associated with the chat profile.
     *
     * @return The name of the groups associated with the chat profile.
     */
    @Unmodifiable
    Set<String> getGroups();

    /**
     * Sets the prefix associated with the chat profile.
     *
     * @param prefix The prefix to set for the chat profile.
     */
    default boolean setPrefix(String prefix) {
        return setPrefix(prefix, 0);
    }

    /**
     * Sets the prefix associated with the chat profile.
     *
     * @param prefix   The prefix to set for the chat profile.
     * @param priority The priority for the prefix. Higher values indicate higher precedence.
     * @see ChatProfile#getPrefix()
     * @see ChatProfile#setPrefix(String)
     */
    boolean setPrefix(String prefix, int priority);

    /**
     * Sets the suffix associated with the chat profile.
     *
     * @param suffix The suffix to set for the chat profile.
     */
    default boolean setSuffix(String suffix) {
        return setSuffix(suffix, 0);
    }

    /**
     * Sets the suffix associated with the chat profile.
     *
     * @param suffix   The suffix to set for the chat profile.
     * @param priority The priority for the suffix. Higher values indicate higher precedence.
     * @see ChatProfile#getSuffix()
     * @see ChatProfile#setSuffix(String)
     */
    boolean setSuffix(String suffix, int priority);
}
