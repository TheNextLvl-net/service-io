package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import net.thenextlvl.service.api.npc.CharacterController;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityDamageCharacterEvent extends CharacterDamageEvent {
    private final Entity attacker;
    private boolean critical;

    // requires CharacterCapability#HEALTH
    public EntityDamageCharacterEvent(CharacterController controller, Character<?> character, Entity attacker,
                                      EntityDamageEvent.DamageCause cause, double damage, boolean critical) {
        super(controller, character, cause, damage);
        this.attacker = attacker;
        this.critical = critical;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public Entity getAttacker() {
        return attacker;
    }
}
