package net.thenextlvl.service.character;

import net.thenextlvl.service.Controller;
import net.thenextlvl.service.capability.CapabilityException;
import net.thenextlvl.service.capability.CapabilityProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages creation, lookup, and lifecycle operations for {@link Character}
 * instances.
 * <p>
 * The API consistently uses the term {@code Character} for the public model,
 * even when a provider internally thinks in terms of NPCs. That keeps the
 * naming aligned with {@link Character} itself and covers both player-shaped
 * characters and non-player entity-backed characters. Provider capabilities
 * still describe the underlying implementation details such as support for
 * {@link CharacterCapability#NON_PLAYER_ENTITIES}.
 */
public interface CharacterController extends CapabilityProvider<CharacterCapability>, Controller {
    /**
     * Creates a character with the given name and entity type.
     * <p>
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param name the name to assign to the character
     * @param type the entity type of the character
     * @return the {@code Character} instance representing the created character
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    default Character createCharacter(final String name, final Class<? extends Entity> type) throws CapabilityException {
        return createCharacter(name, getEntityType(type));
    }

    /**
     * Creates a character with the given name and entity type.
     * <p>
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param name the name to assign to the character
     * @param type the entity type of the character
     * @return the {@code Character} instance representing the created character
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    Character createCharacter(String name, EntityType type) throws CapabilityException;

    /**
     * Spawns a character at the specified location with the given name and entity type.
     * <p>
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param name     the name to assign to the character
     * @param location the location where the character will be spawned
     * @param type     the entity type of the character
     * @return the {@code Character} instance representing the spawned character
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    default Character spawnCharacter(final String name, final Location location, final Class<? extends Entity> type) throws CapabilityException {
        return spawnCharacter(name, location, getEntityType(type));
    }

    /**
     * Spawns a character at the specified location with the given name and entity type.
     * <p>
     * This method may throw a {@code CapabilityException} if the capability
     * {@link CharacterCapability#NON_PLAYER_ENTITIES} is not available.
     *
     * @param name     the name to assign to the character
     * @param location the location where the character will be spawned
     * @param type     the entity type of the character
     * @return the {@code Character} instance representing the spawned character
     * @throws CapabilityException if the operation is not supported due to capability limitations
     */
    Character spawnCharacter(String name, Location location, EntityType type) throws CapabilityException;

    /**
     * Retrieves the managed character represented by the specified entity.
     * <p>
     * This lookup is only meaningful for providers that can associate
     * characters with Bukkit entities.
     *
     * @param entity the entity to retrieve the character for
     * @return an {@code Optional} containing the {@code Character} if the entity is managed as a character
     */
    Optional<Character> getCharacter(Entity entity);

    /**
     * Creates a player character with the specified name.
     *
     * @param name the name to assign to the character
     * @return the {@code Character} instance representing the character
     */
    default Character createCharacter(final String name) {
        return createCharacter(name, EntityType.PLAYER);
    }

    /**
     * Spawns a player character at the specified location.
     *
     * @param name     the name to assign to the character
     * @param location the location where the character will be spawned
     * @return the {@code Character} instance representing the character
     */
    default Character spawnCharacter(final String name, final Location location) {
        return spawnCharacter(name, location, EntityType.PLAYER);
    }

    /**
     * Retrieves a list of all available characters.
     *
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character> getCharacters();

    /**
     * Retrieves all characters that currently qualify to be visible to the
     * specified player.
     *
     * @param player the {@code Player} for which to retrieve the associated characters
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character> getCharacters(Player player);

    /**
     * Retrieves a list of all characters within a specific world.
     *
     * @param world the world in which to locate the characters
     * @return an unmodifiable list of {@code Character} objects
     */
    @Unmodifiable
    List<Character> getCharacters(World world);

    /**
     * Retrieves the character associated with the specified name.
     *
     * @param name the name of the character to retrieve
     * @return an {@code Optional} containing the character or empty if no character exists with the given name
     */
    Optional<Character> getCharacter(String name);

    /**
     * Retrieves the character associated with the specified UUID.
     *
     * @param uuid the unique identifier of the character to retrieve
     * @return an {@code Optional} containing the character or empty if no
     * character exists with the given UUID
     */
    Optional<Character> getCharacter(UUID uuid);

    /**
     * Retrieves the character represented by the specified player entity.
     * <p>
     * This lookup is intended for providers that expose real server-backed
     * player entities via {@link CharacterCapability#ACTUAL_ENTITIES}.
     *
     * @param player the player object for which to retrieve the character
     * @return an {@code Optional} containing the character or empty if the player is not represented as a character
     */
    Optional<Character> getCharacter(Player player);

    /**
     * Determines whether a given entity is currently managed as a character.
     *
     * @param entity the entity to be checked
     * @return true if the entity is a character, false otherwise
     */
    boolean isCharacter(Entity entity);

    private static EntityType getEntityType(final Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid entity type: " + type.getName()));
    }
}
