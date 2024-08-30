package net.thenextlvl.service.model.chat;

import net.thenextlvl.service.api.chat.ChatProfile;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Optional<String> getPrefix() {
        return Optional.ofNullable(holder().getPermissionsHandler().getUserPrefix(user().getLastName()));
    }

    @Override
    public Optional<String> getPrimaryGroup() {
        return Optional.of(user().getGroup().getName());
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(holder().getPermissionsHandler().getUserSuffix(user().getLastName()));
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
