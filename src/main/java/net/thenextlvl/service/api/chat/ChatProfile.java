package net.thenextlvl.service.api.chat;

import net.thenextlvl.service.api.model.Display;
import net.thenextlvl.service.api.model.InfoNode;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

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
