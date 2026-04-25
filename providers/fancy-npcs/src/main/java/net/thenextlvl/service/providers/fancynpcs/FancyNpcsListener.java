package net.thenextlvl.service.providers.fancynpcs;

import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.character.event.PlayerInteractCharacterEvent;
import net.thenextlvl.service.character.event.PlayerInteractCharacterEvent.InteractionType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FancyNpcsListener implements Listener {
    private final CharacterController controller;

    public FancyNpcsListener(final CharacterController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(final NpcInteractEvent event) {
        final var interactionType = event.getInteractionType().equals(ActionTrigger.RIGHT_CLICK)
                ? event.getPlayer().isSneaking() ? InteractionType.SHIFT_RIGHT_CLICK : InteractionType.RIGHT_CLICK
                : event.getPlayer().isSneaking() ? InteractionType.SHIFT_LEFT_CLICK : InteractionType.LEFT_CLICK;
        final var interactEvent = new PlayerInteractCharacterEvent(
                controller, new FancyCharacter(event.getNpc()),
                event.getPlayer(), interactionType
        );
        interactEvent.setCancelled(event.isCancelled());
        event.setCancelled(!interactEvent.callEvent());
    }
}
