package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import net.thenextlvl.service.api.npc.CharacterController;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CharacterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CharacterController controller;
    private final Character<?> character;

    public CharacterEvent(CharacterController controller, Character<?> character) {
        super(!character.getServer().isPrimaryThread());
        this.controller = controller;
        this.character = character;
    }

    public CharacterController getController() {
        return controller;
    }

    public Character<?> getCharacter() {
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
