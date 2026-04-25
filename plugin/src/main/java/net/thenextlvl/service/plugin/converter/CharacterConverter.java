package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.character.CharacterController;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

final class CharacterConverter extends Converter<CharacterController> {
    public CharacterConverter(final Plugin plugin, final CharacterController source, final CharacterController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        source.getCharacters().forEach(character -> target.getCharacter(character.getName()).orElseGet(() -> {
            final var createdCharacter = target.createCharacter(character.getName(), character.getType());
            createdCharacter.setDisplayName(character.getDisplayName());
            createdCharacter.setDisplayRange(character.getDisplayRange());
            createdCharacter.setInvulnerable(character.isInvulnerable());
            createdCharacter.setPersistent(character.isPersistent());
            createdCharacter.setTablistEntryHidden(character.isTablistEntryHidden());
            createdCharacter.setVisibleByDefault(character.isVisibleByDefault());
            character.getLocation().ifPresent(createdCharacter::spawn);
            character.addViewers(createdCharacter.getViewers());
            return createdCharacter;
        }));
        return CompletableFuture.completedFuture(null);
    }
}
