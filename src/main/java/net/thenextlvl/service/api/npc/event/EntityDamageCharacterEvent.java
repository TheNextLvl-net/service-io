package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityDamageCharacterEvent extends CharacterDamageEvent {
    private final Entity attacker;
    private boolean critical;

    public EntityDamageCharacterEvent(Entity attacker, Character<?> character, EntityDamageEvent.DamageCause cause, double damage, boolean critical) {
        super(character, cause, damage);
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
