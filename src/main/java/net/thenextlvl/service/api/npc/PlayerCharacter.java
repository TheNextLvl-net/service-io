package net.thenextlvl.service.api.npc;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PlayerCharacter extends Character, Player {
    boolean isTablistEntryHidden();

    void setTablistEntryHidden(boolean hidden);
}
