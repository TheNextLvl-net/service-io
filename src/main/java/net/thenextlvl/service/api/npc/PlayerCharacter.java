package net.thenextlvl.service.api.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PlayerCharacter extends Character, Player {
    PlayerProfile getProfile();

    boolean isTablistEntryHidden();

    void setProfile(PlayerProfile profile);

    void setTablistEntryHidden(boolean hidden);
}
