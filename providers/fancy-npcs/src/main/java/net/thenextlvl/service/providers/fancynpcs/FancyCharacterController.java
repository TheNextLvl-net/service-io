package net.thenextlvl.service.providers.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.NpcData;
import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterCapability;
import net.thenextlvl.service.api.character.CharacterController;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public final class FancyCharacterController implements CharacterController {
    private final EnumSet<CharacterCapability> capabilities = EnumSet.of(
            CharacterCapability.INTERACTIONS,
            CharacterCapability.NON_PLAYER_ENTITIES
    );
    private final Plugin plugin;

    public FancyCharacterController(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createNPC(final String name, final Class<T> type) {
        return createNPC(name, getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> createNPC(final String name, final EntityType type) {
        final var plugin = FancyNpcsPlugin.get();

        final var location = new Location(this.plugin.getServer().getWorlds().getFirst(), 0, 0, 0);
        final var data = new NpcData(name, new UUID(0, 0), location);
        data.setType(type);
        final var npc = plugin.getNpcAdapter().apply(data);

        plugin.getNpcManager().registerNpc(npc);
        npc.create();
        return new FancyCharacter<>(npc);
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(final String name, final Location location, final Class<T> type) {
        return spawnNPC(name, location, getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(final String name, final Location location, final EntityType type) {
        final var npc = this.<T>createNPC(name, type);
        npc.spawn(location);
        return npc;
    }

    @Override
    public <T extends Entity> Optional<Character<T>> getNPC(final T entity) {
        return Optional.empty();
    }

    @Override
    public Character<Player> createNPC(final String name) {
        return createNPC(name, Player.class);
    }

    @Override
    public Character<Player> spawnNPC(final String name, final Location location) {
        return spawnNPC(name, location, Player.class);
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs() {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(final Player player) {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .filter(npc -> npc.getIsVisibleForPlayer().containsKey(player.getUniqueId()))
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(final World world) {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .filter(npc -> npc.getData().getLocation() != null)
                .filter(npc -> world.equals(npc.getData().getLocation().getWorld()))
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Character<?>> getNPC(final String name) {
        return Optional.ofNullable(FancyNpcsPlugin.get().getNpcManager().getNpc(name))
                .map(FancyCharacter::new);
    }

    @Override
    public Optional<Character<?>> getNPC(final UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Character<Player>> getNPC(final Player player) {
        return Optional.empty();
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

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getName() {
        return "FancyNpcs";
    }

    @Override
    public boolean isNPC(final Entity entity) {
        return false;
    }

    public EntityType getEntityTypeByClass(final Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
    }
}
