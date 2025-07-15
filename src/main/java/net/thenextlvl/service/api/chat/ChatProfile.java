package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.model.Display;
import net.thenextlvl.service.api.model.InfoNode;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a chat profile that provides information about a user involved in a chat system.
 * <p>
 * A ChatProfile contains metadata such as the display name, assigned groups, and primary group.
 * It also allows querying or setting various information nodes related to the profile.
 * <p>
 * This interface extends the {@code InfoNode} and {@code Display} interfaces,
 * inheriting functionalities related to generic information handling and display attributes.
 *
 * @since 1.0.0
 */
@NullMarked
public interface ChatProfile extends InfoNode, Display {
    /**
     * Retrieves the name associated with the chat profile.
     *
     * @return An Optional containing the name of the chat profile.
     * Returns an empty Optional if no name is set.
     */
    Optional<String> getName();

    /**
     * Retrieves the primary group associated with the chat profile.
     *
     * @return An Optional containing the primary group of the chat profile.
     * Returns an empty Optional if no primary group is set.
     */
    Optional<String> getPrimaryGroup();

    /**
     * Retrieves the name of the groups associated with the chat profile.
     *
     * @return The name of the groups associated with the chat profile.
     */
    @Unmodifiable
    Set<String> getGroups();
}
