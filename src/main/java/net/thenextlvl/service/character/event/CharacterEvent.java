package net.thenextlvl.service.character.event;

import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterController;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Base event for operations involving a managed {@link Character}.
 * <p>
 * Every character event exposes the controller that emitted it and the
 * character the event applies to.
 *
 * @since 2.2.0
 */
public abstract class CharacterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CharacterController controller;
    private final Character character;

    /**
     * Constructs a new CharacterEvent.
     *
     * @param controller the character controller responsible for managing the character
     * @param character  the character instance involved in the event
     * @since 3.0.0
     */
    public CharacterEvent(final CharacterController controller, final Character character) {
        super(!Bukkit.isPrimaryThread());
        this.controller = controller;
        this.character = character;
    }

    /**
     * Retrieves the character controller associated with this event.
     *
     * @return the {@code CharacterController} responsible for managing the character in this event.
     * @since 2.2.0
     */
    public CharacterController getController() {
        return controller;
    }

    /**
     * Retrieves the character associated with this event.
     *
     * @return the {@code Character} instance involved in this event
     * @since 3.0.0
     */
    public Character getCharacter() {
        return character;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
