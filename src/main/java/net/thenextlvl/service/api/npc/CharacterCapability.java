package net.thenextlvl.service.api.npc;

import net.kyori.adventure.key.Key;
import net.thenextlvl.service.api.capability.Capability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum CharacterCapability implements Capability {
    NON_PLAYER_ENTITIES(Key.key("capability", "non_player_entities")),
    ;
    private final Key key;

    CharacterCapability(Key key) {
        this.key = key;
    }

    @Override
    public Key key() {
        return this.key;
    }
}
