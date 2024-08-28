package net.thenextlvl.service.wrapper.service.model;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.chat.Chat;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class WrappedChatProfile implements ChatProfile {
    private final @Nullable World world;
    private final Chat chat;
    private final OfflinePlayer holder;

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(holder.getName());
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(chat.getPlayerPrefix(world != null ? world.getName() : null, holder));
    }

    @Override
    public Optional<String> getPrimaryGroup() {
        return Optional.ofNullable(chat.getPrimaryGroup(world != null ? world.getName() : null, holder));
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(chat.getPlayerSuffix(world != null ? world.getName() : null, holder));
    }

    @Override
    public @Unmodifiable Set<String> getGroups() {
        return Set.of(chat.getPlayerGroups(world != null ? world.getName() : null, holder));
    }

    @Override
    public boolean setPrefix(String prefix, int priority) {
        chat.setPlayerPrefix(world != null ? world.getName() : null, holder, prefix);
        return true;
    }

    @Override
    public boolean setSuffix(String suffix, int priority) {
        chat.setPlayerSuffix(world != null ? world.getName() : null, holder, suffix);
        return true;
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper) {
        return Optional.ofNullable(chat.getPlayerInfoString(
                world != null ? world.getName() : null, holder, key, null
        )).map(mapper);
    }

    @Override
    public Optional<Boolean> booleanInfoNode(String key) {
        return Optional.of(chat.getPlayerInfoBoolean(
                world != null ? world.getName() : null, holder, key, false
        ));
    }

    @Override
    public Optional<Double> doubleInfoNode(String key) throws NumberFormatException {
        return Optional.of(chat.getPlayerInfoDouble(
                world != null ? world.getName() : null, holder, key, 0
        ));
    }

    @Override
    public Optional<Integer> intInfoNode(String key) throws NumberFormatException {
        return Optional.of(chat.getPlayerInfoInteger(
                world != null ? world.getName() : null, holder, key, 0
        ));
    }

    @Override
    public boolean removeInfoNode(String key) {
        chat.setPlayerInfoString(world != null ? world.getName() : null, holder, key, null);
        return true;
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        chat.setPlayerInfoString(world != null ? world.getName() : null, holder, key, value);
        return true;
    }
}
