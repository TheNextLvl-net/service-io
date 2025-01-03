package net.thenextlvl.service.controller.character;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterCapability;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.model.character.citizens.CitizensCharacter;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NullMarked
public class CitizensCharacterController implements CharacterController {
    private final EnumSet<CharacterCapability> capabilities = EnumSet.of(
            CharacterCapability.HEALTH,
            CharacterCapability.INTERACTIONS,
            CharacterCapability.NON_PLAYER_ENTITIES,
            CharacterCapability.ACTUAL_ENTITIES
    );
    private final ServicePlugin plugin;

    public CitizensCharacterController(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, Class<T> type) {
        return createNPC(name, plugin.getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, EntityType type) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
        return new CitizensCharacter<>(npc);
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(String name, Location location, Class<T> type) {
        return spawnNPC(name, location, plugin.getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(String name, Location location, EntityType type) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(type, name, location);
        return new CitizensCharacter<>(npc);
    }

    @Override
    public <T extends Entity> Optional<Character<T>> getNPC(T entity) {
        return Optional.ofNullable(CitizensAPI.getNPCRegistry().getNPC(entity))
                .map(CitizensCharacter::new);
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs() {
        return streamNPCs().map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(Player player) {
        return streamNPCs()
                .filter(character -> !character.isHiddenFrom(player))
                .map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(World world) {
        return streamNPCs()
                .filter(character -> world.equals(character.getEntity().getWorld()))
                .map(CitizensCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Character<?>> getNPC(String name) {
        return streamNPCs().filter(npc -> name.equals(npc.getRawName()))
                .<Character<?>>map(CitizensCharacter::new)
                .findAny();
    }

    @Override
    public Character<Player> createNPC(String name) {
        return createNPC(name, Player.class);
    }

    @Override
    public Character<Player> spawnNPC(String name, Location location) {
        return spawnNPC(name, location, Player.class);
    }

    @Override
    public Optional<Character<?>> getNPC(UUID uuid) {
        return Optional.ofNullable(plugin.getServer().getEntity(uuid))
                .map(CitizensAPI.getNPCRegistry()::getNPC)
                .map(CitizensCharacter::new);
    }

    @Override
    public Optional<Character<Player>> getNPC(Player player) {
        return Optional.ofNullable(CitizensAPI.getNPCRegistry().getNPC(player))
                .filter(npc -> npc.getEntity().getType().equals(EntityType.PLAYER))
                .map(CitizensCharacter::new);
    }

    @Override
    public Plugin getPlugin() {
        return CitizensAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Citizens";
    }

    @Override
    public boolean isNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    private static Stream<NPC> streamNPCs() {
        return StreamSupport.stream(CitizensAPI.getNPCRegistries().spliterator(), false)
                .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false));
    }

    @Override
    public @Unmodifiable EnumSet<CharacterCapability> getCapabilities() {
        return EnumSet.copyOf(this.capabilities);
    }

    @Override
    public boolean hasCapabilities(Collection<CharacterCapability> capabilities) {
        return this.capabilities.containsAll(capabilities);
    }

    @Override
    public boolean hasCapability(CharacterCapability capability) {
        return this.capabilities.contains(capability);
    }
}
