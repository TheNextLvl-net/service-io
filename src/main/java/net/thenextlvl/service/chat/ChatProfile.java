package net.thenextlvl.service.chat;

import net.thenextlvl.service.model.Display;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;

public interface ChatProfile extends Display {
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
     * Retrieves the world associated with the chat profile.
     *
     * @return An Optional containing the world of the chat profile.
     * @since 3.0.0
     */
    Optional<World> getWorld();

    /**
     * Retrieves the name of the groups associated with the chat profile.
     *
     * @return The name of the groups associated with the chat profile.
     */
    @Unmodifiable
    Set<String> getGroups();
}
