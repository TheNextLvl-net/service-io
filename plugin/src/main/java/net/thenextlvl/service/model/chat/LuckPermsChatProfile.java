package net.thenextlvl.service.model.chat;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record LuckPermsChatProfile(User user, QueryOptions options) implements ChatProfile {
    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(user().getUsername());
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(user().getCachedData().getMetaData(options()).getPrefix());
    }

    @Override
    public Optional<String> getPrimaryGroup() {
        return Optional.of(user().getPrimaryGroup());
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(user().getCachedData().getMetaData(options()).getSuffix());
    }

    @Override
    public Set<String> getGroups() {
        return user().getInheritedGroups(options()).stream()
                .map(Group::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean setPrimaryGroup(@NotNull String group) {
        return user().setPrimaryGroup(group).wasSuccessful();
    }

    @Override
    public boolean setPrefix(String prefix, int priority) {
        var result = user().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean setSuffix(String suffix, int priority) {
        var result = user().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<String, @Nullable T> mapper) {
        return user().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        var result = user().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(String key) {
        user().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean removeInfoNode(String key, String value) {
        var result = user().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }
}
