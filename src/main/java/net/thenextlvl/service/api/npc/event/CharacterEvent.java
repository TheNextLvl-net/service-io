package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Character character;

    public CharacterEvent(Character character) {
        super(!character.getServer().isPrimaryThread());
        this.character = character;
    }

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
