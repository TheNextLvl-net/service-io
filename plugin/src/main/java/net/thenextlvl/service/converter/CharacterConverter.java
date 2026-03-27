package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.character.CharacterController;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class CharacterConverter implements Converter<CharacterController> {

    @Override
    public CompletableFuture<Void> convert(final CharacterController source, final CharacterController target) {
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
