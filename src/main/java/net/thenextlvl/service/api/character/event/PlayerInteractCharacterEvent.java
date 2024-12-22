package net.thenextlvl.service.api.character.event;

import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterCapability;
import net.thenextlvl.service.api.character.CharacterController;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event triggered when a player interacts with a character.
 * <p>
 * This event occurs when a player performs an interaction with a specific character,
 * such as a left-click or a right-click.
 * It encapsulates information about the player performing the interaction,
 * the type of interaction, and the character involved.
 * <p>
 * This event is cancellable, meaning that handling methods may prevent
 * the interaction from proceeding by setting the event's cancelled state.
 * <p>
 * This event will only be fired for providers that support the {@link CharacterCapability#INTERACTIONS} capability.
 */
@NullMarked
public class PlayerInteractCharacterEvent extends CharacterEvent implements Cancellable {
    private final Player player;
    private final InteractionType type;
    private boolean cancelled;

    /**
     * Constructs an event representing a player's interaction with a character.
     *
     * @param controller the character controller managing the character involved in the event
     * @param character  the character being interacted with
     * @param player     the player performing the interaction
     * @param type       the type of interaction performed by the player
     */
    public PlayerInteractCharacterEvent(CharacterController controller, Character<?> character,
                                        Player player, InteractionType type) {
        super(controller, character);
        this.player = player;
        this.type = type;
    }

    /**
     * Retrieves the player involved in the event.
     *
     * @return the {@code Player} instance that is performing the interaction in this event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the type of interaction performed during the event.
     *
     * @return the {@code InteractionType} representing the type of interaction
     */
    public InteractionType getType() {
        return type;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Represents the type of interaction performed by a player.
     */
    public enum InteractionType {
        /**
         * Represents a left-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player attacks.
         */
        LEFT_CLICK,

        /**
         * Represents a right-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player interacts.
         */
        RIGHT_CLICK,

        /**
         * Represents a shift-left-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player attacks while sneaking.
         */
        SHIFT_LEFT_CLICK,

        /**
         * Represents a shift-right-click interaction performed by a player.
         * <p>
         * This type of interaction occurs when a player interacts while sneaking.
         */
        SHIFT_RIGHT_CLICK;

        /**
         * Checks if the interaction type is a left-click.
         *
         * @return whether this interaction represents a left-click
         */
        public boolean isLeftClick() {
            return equals(LEFT_CLICK) || equals(SHIFT_LEFT_CLICK);
        }

        /**
         * Checks if the interaction type is a right-click.
         *
         * @return whether this interaction represents a right-click
         */
        public boolean isRightClick() {
            return equals(RIGHT_CLICK) || equals(SHIFT_RIGHT_CLICK);
        }

        /**
         * Checks if the interaction type is a shift-click.
         *
         * @return whether this interaction represents either a shift-left-click or a shift-right-click
         */
        public boolean isShiftClick() {
            return equals(SHIFT_LEFT_CLICK) || equals(SHIFT_RIGHT_CLICK);
        }
    }
}
