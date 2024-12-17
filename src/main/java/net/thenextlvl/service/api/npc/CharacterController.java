package net.thenextlvl.service.api.npc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface CharacterController {
    Character createNPC(String name, Location location, EntityType type);

    @Unmodifiable
    List<Character> getNPCs();

    @Unmodifiable
    List<Character> getNPCs(Player player);

    @Unmodifiable
    List<Character> getNPCs(World world);

    Optional<Character> getNPC(Entity entity);

    Optional<Character> getNPC(UUID uuid);

    boolean isNPC(Entity entity);
}
