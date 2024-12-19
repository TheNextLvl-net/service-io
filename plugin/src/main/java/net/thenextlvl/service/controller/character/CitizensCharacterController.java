package net.thenextlvl.service.controller.character;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.npc.Character;
import net.thenextlvl.service.api.npc.CharacterController;
import net.thenextlvl.service.model.character.citizens.CitizensCharacter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NullMarked
public class CitizensCharacterController implements CharacterController {
    private final ServicePlugin plugin;

    public CitizensCharacterController(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, Class<T> type) {
        return createNPC(name, getType(type));
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, EntityType type) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
        return new CitizensCharacter<>(npc);
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(String name, Location location, Class<T> type) {
        return spawnNPC(name, location, getType(type));
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

    private EntityType getType(Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
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
}
