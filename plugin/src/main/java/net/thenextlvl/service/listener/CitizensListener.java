package net.thenextlvl.service.listener;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.npc.event.CharacterDamageEvent;
import net.thenextlvl.service.api.npc.event.EntityDamageCharacterEvent;
import net.thenextlvl.service.api.npc.event.PlayerInteractCharacterEvent;
import net.thenextlvl.service.model.character.citizens.CitizensCharacter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CitizensListener implements Listener {
    private final ServicePlugin plugin;

    public CitizensListener(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(NPCClickEvent event) {
        var character = new CitizensCharacter<>(event.getNPC());
        var characterEvent = new PlayerInteractCharacterEvent(
                event.getClicker(), character
        );
        characterEvent.setCancelled(event.isCancelled());
        event.setCancelled(!characterEvent.callEvent());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(NPCDamageByEntityEvent event) {
        var cause = (EntityDamageByEntityEvent) event.getEvent();
        var character = new CitizensCharacter<>(event.getNPC());
        var characterEvent = new EntityDamageCharacterEvent(
                event.getDamager(), character, event.getCause(),
                event.getDamage(), cause.isCritical()
        );
        characterEvent.setCancelled(event.isCancelled());
        event.setCancelled(!characterEvent.callEvent());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCInteract(NPCDamageEvent event) {
        var character = new CitizensCharacter<>(event.getNPC());
        var characterEvent = new CharacterDamageEvent(
                character, event.getCause(), event.getDamage()
        );
        characterEvent.setCancelled(event.isCancelled());
        event.setCancelled(!characterEvent.callEvent());
    }
}
