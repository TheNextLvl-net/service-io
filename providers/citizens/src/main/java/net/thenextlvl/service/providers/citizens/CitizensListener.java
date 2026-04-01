package net.thenextlvl.service.providers.citizens;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.character.event.CharacterDamageEvent;
import net.thenextlvl.service.character.event.EntityDamageCharacterEvent;
import net.thenextlvl.service.character.event.PlayerInteractCharacterEvent;
import net.thenextlvl.service.character.event.PlayerInteractCharacterEvent.InteractionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CitizensListener implements Listener {
    private final CharacterController controller;

    public CitizensListener(final CharacterController controller) {
        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCRightClick(final NPCRightClickEvent event) {
        onNPCClick(event, event.getClicker().isSneaking()
                ? InteractionType.SHIFT_RIGHT_CLICK
                : InteractionType.RIGHT_CLICK);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCDamageByEntity(final NPCDamageByEntityEvent event) {
        final var cause = (EntityDamageByEntityEvent) event.getEvent();
        final var character = new CitizensCharacter<>(event.getNPC());
        final var damageEvent = new EntityDamageCharacterEvent(
                controller, character, event.getDamager(), event.getCause(),
                event.getDamage(), cause.isCritical()
        );
        damageEvent.setCancelled(event.isCancelled());
        event.setCancelled(!damageEvent.callEvent());
        event.setDamage(damageEvent.getDamage());

        if (!(event.getDamager() instanceof final Player player)) return;
        final var clickEvent = new NPCLeftClickEvent(event.getNPC(), player);
        clickEvent.setCancelled(event.isCancelled());
        onNPCClick(clickEvent, event.getDamager().isSneaking()
                ? InteractionType.SHIFT_LEFT_CLICK
                : InteractionType.LEFT_CLICK);
        if (clickEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onNPCDamage(final NPCDamageEvent event) {
        final var character = new CitizensCharacter<>(event.getNPC());
        final var damageEvent = new CharacterDamageEvent(
                controller, character, event.getCause(), event.getDamage()
        );
        damageEvent.setCancelled(event.isCancelled());
        event.setCancelled(!damageEvent.callEvent());
    }

    private void onNPCClick(final NPCClickEvent event, final InteractionType type) {
        final var character = new CitizensCharacter<>(event.getNPC());
        final var characterEvent = new PlayerInteractCharacterEvent(
                controller, character, event.getClicker(), type
        );
        characterEvent.setCancelled(event.isCancelled());
        event.setCancelled(!characterEvent.callEvent());
    }
}
