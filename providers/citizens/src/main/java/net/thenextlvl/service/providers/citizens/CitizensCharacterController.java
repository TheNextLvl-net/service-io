package net.thenextlvl.service.providers.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterCapability;
import net.thenextlvl.service.character.CharacterController;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NullMarked
public final class CitizensCharacterController implements CharacterController {
    private final EnumSet<CharacterCapability> capabilities = EnumSet.of(
            CharacterCapability.HEALTH,
            CharacterCapability.INTERACTIONS,
            CharacterCapability.NON_PLAYER_ENTITIES,
            CharacterCapability.ACTUAL_ENTITIES
    );
    private final Plugin plugin;

    public CitizensCharacterController(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Character createCharacter(final String name, final EntityType type) {
        final var npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
        return new CitizensCharacter(npc);
    }

    @Override
    public Character spawnCharacter(final String name, final Location location, final EntityType type) {
        final var npc = CitizensAPI.getNPCRegistry().createNPC(type, name, location);
        return new CitizensCharacter(npc);
    }

    @Override
    public Optional<Character> getCharacter(final Entity entity) {
        return Optional.ofNullable(CitizensAPI.getNPCRegistry().getNPC(entity))
                .map(CitizensCharacter::new);
    }

    @Override
    public @Unmodifiable List<Character> getCharacters() {
        return streamNPCs().map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character> getCharacters(final Player player) {
        return streamNPCs()
                .filter(character -> !character.isHiddenFrom(player))
                .map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character> getCharacters(final World world) {
        return streamNPCs()
                .filter(character -> world.equals(character.getEntity().getWorld()))
                .map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Character> getCharacter(final String name) {
        return streamNPCs().filter(npc -> name.equals(npc.getRawName()))
                .<Character>map(CitizensCharacter::new)
                .findAny();
    }

    @Override
    public Optional<Character> getCharacter(final UUID uuid) {
        return Optional.ofNullable(plugin.getServer().getEntity(uuid))
                .map(CitizensAPI.getNPCRegistry()::getNPC)
                .map(CitizensCharacter::new);
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getName() {
        return "Citizens";
    }

    @Override
    public boolean isCharacter(final Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    private static Stream<NPC> streamNPCs() {
        return StreamSupport.stream(CitizensAPI.getNPCRegistries().spliterator(), false)
                .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false));
    }

    @Override
    public @Unmodifiable Set<CharacterCapability> getCapabilities() {
        return Set.copyOf(capabilities);
    }

    @Override
    public boolean hasCapabilities(final Collection<CharacterCapability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(final CharacterCapability capability) {
        return this.capabilities.contains(capability);
    }
}
