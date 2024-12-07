package net.thenextlvl.service.api.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public interface CharacterController {
    Character createCharacter(Location location, EntityType type);

    PlayerCharacter createCharacter(Location location, PlayerProfile profile);

    PlayerCharacter createCharacter(Location location, UUID uuid, String name);
}
