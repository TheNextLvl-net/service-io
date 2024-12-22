package net.thenextlvl.service.api.npc.event;

import net.thenextlvl.service.api.npc.Character;
import net.thenextlvl.service.api.npc.CharacterController;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerInteractCharacterEvent extends CharacterEvent implements Cancellable {
    private final Player player;
    private final InteractionType type;
    private boolean cancelled;

    // requires CharacterCapability#INTERACTIONS
    public PlayerInteractCharacterEvent(CharacterController controller, Character<?> character,
                                        Player player, InteractionType type) {
        super(controller, character);
        this.player = player;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

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

    public enum InteractionType {
        LEFT_CLICK,
        RIGHT_CLICK;

        public boolean isLeftClick() {
            return equals(LEFT_CLICK);
        }

        public boolean isRightClick() {
            return equals(RIGHT_CLICK);
        }
    }
}
