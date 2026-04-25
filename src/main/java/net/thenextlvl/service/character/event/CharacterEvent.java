package net.thenextlvl.service.character.event;

import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterController;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a base class for events related to a character in the game.
 * <p>
 * This class serves as a foundation for character-based events, providing
 * common functionality through the controller and character references.
 * It allows for tracking the controller, managing the character, and the target character
 * involved in the event.
 * <p>
 * Subclasses can make use of these common properties while implementing specific
 * character-related event functionalities.
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
     */
    public CharacterController getController() {
        return controller;
    }

    /**
     * Retrieves the character associated with this event.
     *
     * @return the {@code Character} instance involved in this event
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
