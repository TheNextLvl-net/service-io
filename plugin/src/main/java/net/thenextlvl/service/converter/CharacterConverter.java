package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.character.CharacterController;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class CharacterConverter extends Converter<CharacterController> {
    public CharacterConverter(final Plugin plugin, final CharacterController source, final CharacterController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        source.getNPCs().forEach(character -> target.getNPC(character.getName()).orElseGet(() -> {
            final var npc = target.createNPC(character.getName(), character.getType());
            npc.setDisplayName(character.getDisplayName());
            npc.setDisplayRange(character.getDisplayRange());
            npc.setInvulnerable(character.isInvulnerable());
            npc.setPersistent(character.isPersistent());
            npc.setTablistEntryHidden(character.isTablistEntryHidden());
            npc.setVisibleByDefault(character.isVisibleByDefault());
            if (character.getLocation() != null) npc.spawn(character.getLocation());
            character.addViewers(npc.getViewers());
            return npc;
        }));
        return CompletableFuture.completedFuture(null);
    }
}
