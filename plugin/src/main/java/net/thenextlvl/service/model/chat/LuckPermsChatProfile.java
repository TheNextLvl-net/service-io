package net.thenextlvl.service.model.chat;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.model.group.LuckPermsGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
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
    public Set<Group> getGroups() {
        return user().getInheritedGroups(options()).stream()
                .map(group -> new LuckPermsGroup(group, options()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean setPrimaryGroup(@NotNull String group) {
        return user().setPrimaryGroup(group).wasSuccessful();
    }

    @Override
    public void setPrefix(String prefix, int priority) {
        user().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
    }

    @Override
    public void setSuffix(String suffix, int priority) {
        user().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
    }
}
