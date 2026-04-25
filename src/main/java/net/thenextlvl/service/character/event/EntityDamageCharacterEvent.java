package net.thenextlvl.service.character.event;

import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterCapability;
import net.thenextlvl.service.character.CharacterController;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Represents an event triggered when an entity damages a character.
 * <p>
 * This event extends {@link CharacterDamageEvent} and provides additional details
 * about the attacker and whether the damage dealt is critical.
 * It can be used to inspect and modify the damage attributed to an entity's attack and to check the critical status.
 * <p>
 * This event will only be fired for providers that support the {@link CharacterCapability#HEALTH} capability.
 *
 * @since 2.2.0
 */
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
     * @since 3.0.0
     */
    public EntityDamageCharacterEvent(final CharacterController controller, final Character character, final Entity attacker,
                                      final EntityDamageEvent.DamageCause cause, final double damage, final boolean critical) {
        super(controller, character, cause, damage);
        this.attacker = attacker;
        this.critical = critical;
    }

    /**
     * Determines whether the damage associated with this event is critical.
     *
     * @return {@code true} if the damage is a critical hit; {@code false} otherwise
     * @since 2.2.0
     */
    public boolean isCritical() {
        return critical;
    }

    /**
     * Retrieves the entity responsible for causing the damage in this event.
     *
     * @return the {@code Entity} that attacked the character, causing the damage
     * @since 2.2.0
     */
    public Entity getAttacker() {
        return attacker;
    }
}
