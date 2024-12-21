package net.thenextlvl.service.api.npc;

import net.thenextlvl.service.api.capability.CapabilityProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CharacterController extends CapabilityProvider<CharacterCapability> {
    <T extends Entity> Character<T> createNPC(String name, Class<T> type);

    <T extends Entity> Character<T> createNPC(String name, EntityType type);

    <T extends Entity> Character<T> spawnNPC(String name, Location location, Class<T> type);

    <T extends Entity> Character<T> spawnNPC(String name, Location location, EntityType type);

    <T extends Entity> Optional<Character<T>> getNPC(T entity);

    Character<Player> createNPC(String name);

    Character<Player> spawnNPC(String name, Location location);

    @Unmodifiable
    List<Character<?>> getNPCs();

    @Unmodifiable
    List<Character<?>> getNPCs(Player player);

    @Unmodifiable
    List<Character<?>> getNPCs(World world);

    Optional<Character<?>> getNPC(UUID uuid);

    Optional<Character<Player>> getNPC(Player player);

    @Override
    @Unmodifiable
    EnumSet<CharacterCapability> getCapabilities();

    String getName();

    boolean isNPC(Entity entity);
}
