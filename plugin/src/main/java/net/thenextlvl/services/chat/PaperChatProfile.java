package net.thenextlvl.services.chat;

import net.thenextlvl.services.api.chat.ChatProfile;
import net.thenextlvl.services.api.permission.Group;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record PaperChatProfile(OfflinePlayer player) implements ChatProfile {

    @Override
    public @Nullable Group getPrimaryGroup() {
        return null;
    }

    @Override
    public Set<Group> getGroups() {
        return Set.of();
    }

    @Override
    public @Nullable String getName() {
        return player.getName();
    }

    @Override
    public @Nullable String getPrefix() {
        return null;
    }

    @Override
    public @Nullable String getSuffix() {
        return null;
    }

    @Override
    public void setPrefix(@Nullable String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSuffix(@Nullable String suffix) {
        throw new UnsupportedOperationException();
    }
}
