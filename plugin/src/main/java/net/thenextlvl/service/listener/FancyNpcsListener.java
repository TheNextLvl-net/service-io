package net.thenextlvl.service.listener;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.npc.event.EntityDamageCharacterEvent;
import net.thenextlvl.service.api.npc.event.PlayerInteractCharacterEvent;
import net.thenextlvl.service.model.character.fancy.FancyCharacter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FancyNpcsListener implements Listener {
    private final ServicePlugin plugin;

    public FancyNpcsListener(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(NpcInteractEvent event) {
        var characterEvent = switch (event.getInteractionType()) {
            case LEFT_CLICK -> new EntityDamageCharacterEvent(
                    event.getPlayer(), new FancyCharacter<>(event.getNpc()),
                    EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0, false
            );
            case RIGHT_CLICK -> new PlayerInteractCharacterEvent(
                    event.getPlayer(), new FancyCharacter<>(event.getNpc())
            );
            default -> null;
        };
        if (characterEvent != null) event.setCancelled(!characterEvent.callEvent());
    }
}
