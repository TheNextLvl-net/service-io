package net.thenextlvl.service.api.character.event;

import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterCapability;
import net.thenextlvl.service.api.character.CharacterController;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * The CharacterDamageEvent represents an event triggered when a character takes damage.
 * <p>
 * The event allows for inspecting and modifying the damage dealt, determining the cause of
 * the damage, and controlling whether the event should be cancelled.
 * <p>
 * This event will only be fired for providers that support the {@link CharacterCapability#HEALTH} capability.
 *
 * @since 2.2.0
 */
@NullMarked
public class CharacterDamageEvent extends CharacterEvent implements Cancellable {
    private final EntityDamageEvent.DamageCause cause;
    private boolean cancelled;
    private double damage;

    /**
     * Constructs a new CharacterDamageEvent representing damage dealt to a character.
     *
     * @param controller the character controller managing the character
     * @param character  the character that is taking damage
     * @param cause      the cause of the damage
     * @param damage     the amount of damage dealt to the character
     */
    public CharacterDamageEvent(CharacterController controller, Character<?> character, EntityDamageEvent.DamageCause cause, double damage) {
        super(controller, character);
        this.cause = cause;
        this.damage = damage;
    }

    /**
     * Retrieves the cause of the damage associated with this event.
     *
     * @return the {@link EntityDamageEvent.DamageCause} that represents the underlying cause of the damage
     */
    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    /**
     * Retrieves the amount of damage associated with this event.
     *
     * @return the damage value as a double
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Sets the amount of damage for this event.
     *
     * @param damage the new damage value to be set
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
