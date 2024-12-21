package net.thenextlvl.service.controller.character;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.NpcData;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.npc.Character;
import net.thenextlvl.service.api.npc.CharacterCapability;
import net.thenextlvl.service.api.npc.CharacterController;
import net.thenextlvl.service.model.character.fancy.FancyCharacter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class FancyCharacterController implements CharacterController {
    private final EnumSet<CharacterCapability> capabilities = EnumSet.of(
            CharacterCapability.NON_PLAYER_ENTITIES
    );
    private final ServicePlugin plugin;

    public FancyCharacterController(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, Class<T> type) {
        return createNPC(name, plugin.getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> createNPC(String name, EntityType type) {
        var plugin = FancyNpcsPlugin.get();

        var location = new Location(this.plugin.getServer().getWorlds().getFirst(), 0, 0, 0);
        var data = new NpcData(name, new UUID(0, 0), location);
        data.setType(type);
        var npc = plugin.getNpcAdapter().apply(data);

        plugin.getNpcManager().registerNpc(npc);
        npc.create();
        return new FancyCharacter<>(npc);
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(String name, Location location, Class<T> type) {
        return spawnNPC(name, location, plugin.getEntityTypeByClass(type));
    }

    @Override
    public <T extends Entity> Character<T> spawnNPC(String name, Location location, EntityType type) {
        var npc = this.<T>createNPC(name, type);
        npc.spawn(location);
        return npc;
    }

    @Override
    public <T extends Entity> Optional<Character<T>> getNPC(T entity) {
        return Optional.empty();
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
    public @Unmodifiable List<Character<?>> getNPCs() {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(Player player) {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .filter(npc -> npc.getIsVisibleForPlayer().containsKey(player.getUniqueId()))
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @Unmodifiable List<Character<?>> getNPCs(World world) {
        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .filter(npc -> npc.getData().getLocation() != null)
                .filter(npc -> world.equals(npc.getData().getLocation().getWorld()))
                .map(FancyCharacter::new)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Character<?>> getNPC(String name) {
        return Optional.ofNullable(FancyNpcsPlugin.get().getNpcManager().getNpc(name))
                .map(FancyCharacter::new);
    }

    @Override
    public Optional<Character<?>> getNPC(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Character<Player>> getNPC(Player player) {
        return Optional.empty();
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

    @Override
    public String getName() {
        return "FancyNpcs";
    }

    @Override
    public boolean isNPC(Entity entity) {
        return false;
    }
}
