package net.thenextlvl.service.model.chat;

import net.thenextlvl.service.api.chat.ChatProfile;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public record GroupManagerChatProfile(User user, WorldDataHolder holder) implements ChatProfile {
    @Override
    public Optional<String> getDisplayName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(user().getLastName());
    }

    @Override
    public Optional<String> getPrefix(int priority) {
        return Optional.ofNullable(holder().getPermissionsHandler().getUserPrefix(user().getLastName()));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getPrefixes() {
        return getPrefix().map(prefix -> Map.of(0, prefix)).orElseGet(Map::of);
    }

    @Override
    public Optional<String> getPrimaryGroup() {
        return Optional.of(user().getGroup().getName());
    }

    @Override
    public Optional<String> getSuffix(int priority) {
        return Optional.ofNullable(holder().getPermissionsHandler().getUserSuffix(user().getLastName()));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getSuffixes() {
        return getSuffix().map(suffix -> Map.of(0, suffix)).orElseGet(Map::of);
    }

    @Override
    public @Unmodifiable Set<String> getGroups() {
        return user().getSaveSubGroupsList().stream()
                .map(holder()::getGroup)
                .filter(Objects::nonNull)
                .map(Group::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean setDisplayName(@Nullable String displayName) {
        return false;
    }

    @Override
    public boolean setPrefix(@Nullable String prefix, int priority) {
        if (prefix == null) return removeInfoNode("prefix");
        return setInfoNode("prefix", prefix);
    }

    @Override
    public boolean setSuffix(@Nullable String suffix, int priority) {
        if (suffix == null) return removeInfoNode("suffix");
        return setInfoNode("suffix", suffix);
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper) {
        return Optional.ofNullable(mapper.apply(user().getVariables().getVarString(key)));
    }

    @Override
    public boolean removeInfoNode(String key) {
        if (!hasInfoNode(key)) return false;
        user().getVariables().removeVar(key);
        return true;
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        user().getVariables().addVar(key, value);
        return true;
    }

    @Override
    public boolean hasInfoNode(String key) {
        return user().getVariables().hasVar(key);
    }
}
