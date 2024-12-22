package net.thenextlvl.service.api.character.event;

import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterCapability;
import net.thenextlvl.service.api.character.CharacterController;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event triggered when an entity damages a character.
 * <p>
 * This event extends {@link CharacterDamageEvent} and provides additional details
 * about the attacker and whether the damage dealt is critical.
 * It can be used to inspect and modify the damage attributed to an entity's attack and to check the critical status.
 * <p>
 * This event will only be fired for providers that support the {@link CharacterCapability#HEALTH} capability.
 */
@NullMarked
public class EntityDamageCharacterEvent extends CharacterDamageEvent {
    private final Entity attacker;
    private final boolean critical;

    /**
     * Constructs an EntityDamageCharacterEvent.
     *
     * @param controller the CharacterController managing the character involved in the event
     * @param character  the character that is being damaged
     * @param attacker   the entity causing the damage to the character
     * @param cause      the cause of the damage
     * @param damage     the amount of damage dealt to the character
     * @param critical   whether the damage dealt is a critical hit
     */
    public EntityDamageCharacterEvent(CharacterController controller, Character<?> character, Entity attacker,
                                      EntityDamageEvent.DamageCause cause, double damage, boolean critical) {
        super(controller, character, cause, damage);
        this.attacker = attacker;
        this.critical = critical;
    }

    /**
     * Determines whether the damage associated with this event is critical.
     *
     * @return {@code true} if the damage is a critical hit; {@code false} otherwise
     */
    public boolean isCritical() {
        return critical;
    }

    /**
     * Retrieves the entity responsible for causing the damage in this event.
     *
     * @return the {@code Entity} that attacked the character, causing the damage
     */
    public Entity getAttacker() {
        return attacker;
    }
}
