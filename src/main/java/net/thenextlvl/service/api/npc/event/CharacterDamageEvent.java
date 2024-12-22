package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterDamageEvent extends CharacterEvent implements Cancellable {
    private final EntityDamageEvent.DamageCause cause;
    private boolean cancelled;
    private double damage;

    public CharacterDamageEvent(Character<?> character, EntityDamageEvent.DamageCause cause, double damage) {
        super(character);
        this.cause = cause;
        this.damage = damage;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public double getDamage() {
        return damage;
    }

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
