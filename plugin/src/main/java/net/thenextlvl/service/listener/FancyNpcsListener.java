package net.thenextlvl.service.listener;

import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.character.event.PlayerInteractCharacterEvent;
import net.thenextlvl.service.model.character.fancy.FancyCharacter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FancyNpcsListener implements Listener {
    private final CharacterController controller;

    public FancyNpcsListener(CharacterController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(NpcInteractEvent event) {
        var interactionType = event.getInteractionType().equals(ActionTrigger.RIGHT_CLICK)
                ? PlayerInteractCharacterEvent.InteractionType.RIGHT_CLICK
                : PlayerInteractCharacterEvent.InteractionType.LEFT_CLICK;
        var interactEvent = new PlayerInteractCharacterEvent(
                controller, new FancyCharacter<>(event.getNpc()),
                event.getPlayer(), interactionType
        );
        interactEvent.setCancelled(event.isCancelled());
        event.setCancelled(!interactEvent.callEvent());
    }
}
