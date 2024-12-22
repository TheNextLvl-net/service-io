package net.thenextlvl.service.api.character;

import net.thenextlvl.service.api.capability.CapabilityException;
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

/**
 * The {@code CharacterController} interface defines methods for managing
 * and interacting with non-player characters (NPCs).
 * It includes functionality for creating, spawning, retrieving, and checking
 * entities as NPCs, along with capability management.
 */
@NullMarked
public interface CharacterController extends CapabilityProvider<CharacterCapability> {
    /**
     * Creates a character with the given name and entity type.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param <T>      the type of the entity being created as an NPC
     * @param name     the name to assign to the NPC
     * @param type     the entity type of the NPC
     * @return the {@code Character} instance representing the created NPC
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    <T extends Entity> Character<T> createNPC(String name, Class<T> type) throws CapabilityException;

    /**
     * Creates a character with the given name and entity type.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param <T>      the type of the entity being created as an NPC
     * @param name     the name to assign to the NPC
     * @param type     the entity type of the NPC
     * @return the {@code Character} instance representing the created NPC
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    <T extends Entity> Character<T> createNPC(String name, EntityType type) throws CapabilityException;

    /**
     * Spawns a character at the specified location with the given name and entity type.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param <T>      the type of the entity being spawned as an NPC
     * @param name     the name to assign to the NPC
     * @param location the location where the NPC will be spawned
     * @param type     the entity type of the NPC
     * @return the {@code Character} instance representing the spawned NPC
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    <T extends Entity> Character<T> spawnNPC(String name, Location location, Class<T> type) throws CapabilityException;

    /**
     * Spawns a character at the specified location with the given name and entity type.
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param <T>      the type of the entity being spawned as an NPC
     * @param name     the name to assign to the NPC
     * @param location the location where the NPC will be spawned
     * @param type     the entity type of the NPC
     * @return the {@code Character} instance representing the spawned NPC
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    <T extends Entity> Character<T> spawnNPC(String name, Location location, EntityType type) throws CapabilityException;

    /**
     * Retrieves the {@code Character} representation of the specified entity if it is an NPC.
     *
     * @param entity the entity to retrieve the NPC for
     * @param <T>    the type of the entity
     * @return an {@code Optional} containing the {@code Character} if the entity is an NPC
     */
    <T extends Entity> Optional<Character<T>> getNPC(T entity);

    /**
     * Creates a player character with the specified name.
     *
     * @param name the name to assign to the NPC
     * @return the {@code Character} instance representing the NPC
     */
    Character<Player> createNPC(String name);

    /**
     * Spawns a player character at the specified location.
     *
     * @param name     the name to assign to the NPC
     * @param location the location where the NPC will be spawned
     * @return the {@code Character} instance representing the NPC
     */
    Character<Player> spawnNPC(String name, Location location);

    /**
     * Retrieves a list of all available NPCs.
     *
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character<?>> getNPCs();

    /**
     * Retrieves a list of all NPCs that may be visible to the specified player.
     *
     * @param player the {@code Player} for which to retrieve the associated NPCs
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character<?>> getNPCs(Player player);

    /**
     * Retrieves a list of all NPCs within a specific world.
     *
     * @param world the world in which to locate the NPCs
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character<?>> getNPCs(World world);

    /**
     * Retrieves the character associated with the specified name.
     *
     * @param name the name of the NPC to retrieve
     * @return an {@code Optional} containing the NPC or empty if no NPC exists with the given name
     */
    Optional<Character<?>> getNPC(String name);

    /**
     * Retrieves the character associated with the specified uuid.
     *
     * @param uuid the unique id of the character to retrieve
     * @return an {@code Optional} containing the NPC or empty if no NPC exists with the given uuid
     */
    Optional<Character<?>> getNPC(UUID uuid);

    /**
     * Retrieves the character object represented by the specified player.
     * This method requires the provider to support the {@link CharacterCapability#ACTUAL_ENTITIES} capability.
     *
     * @param player the player object for which to retrieve the character
     * @return an {@code Optional} containing the NPC or empty if the player is not an NPC
     */
    Optional<Character<Player>> getNPC(Player player);

    @Override
    @Unmodifiable
    EnumSet<CharacterCapability> getCapabilities();

    /**
     * Retrieves the name associated with the character controller.
     *
     * @return the name of the character controller.
     */
    String getName();

    /**
     * Determines whether a given entity is a non-player character (NPC).
     *
     * @param entity the entity to be checked
     * @return true if the entity is an NPC, false otherwise
     */
    boolean isNPC(Entity entity);
}
