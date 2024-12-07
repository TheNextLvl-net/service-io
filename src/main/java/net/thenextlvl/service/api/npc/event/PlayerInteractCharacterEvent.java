package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerInteractCharacterEvent extends CharacterEvent implements Cancellable {
    private final Player player;
    private boolean cancelled;

    public PlayerInteractCharacterEvent(Player player, Character character) {
        super(character);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
