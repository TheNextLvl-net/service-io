package net.thenextlvl.service.providers.luckperms;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public record LuckPermsChatProfile(User user, QueryOptions options) implements ChatProfile {
    @Override
    public Optional<String> getDisplayName() {
        return getInfoNode("DISPLAY_NAME");
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(user().getUsername());
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(user().getCachedData().getMetaData(options()).getPrefix());
    }

    @Override
    public Optional<String> getPrefix(final int priority) {
        return Optional.ofNullable(getPrefixes().get(priority));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getPrefixes() {
        return user().getCachedData().getMetaData(options()).getPrefixes();
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
    public Optional<String> getSuffix(final int priority) {
        return Optional.ofNullable(getSuffixes().get(priority));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getSuffixes() {
        return user().getCachedData().getMetaData(options()).getSuffixes();
    }

    @Override
    public Set<String> getGroups() {
        return user().getInheritedGroups(options()).stream()
                .map(Group::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean setDisplayName(@Nullable final String displayName) {
        if (displayName == null) return unsetDisplayName();
        final var result = user().data().add(DisplayNameNode.builder(displayName).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    private boolean unsetDisplayName() {
        user().data().clear(options().context(), node -> node instanceof DisplayNameNode);
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean setPrefix(@Nullable final String prefix, final int priority) {
        if (prefix == null) return unsetPrefix(priority);
        final var result = user().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    private boolean unsetPrefix(final int priority) {
        user().data().clear(options().context(), node -> node instanceof final PrefixNode prefix && prefix.getPriority() == priority);
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean setSuffix(@Nullable final String suffix, final int priority) {
        if (suffix == null) return unsetSuffix(priority);
        final var result = user().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    private boolean unsetSuffix(final int priority) {
        user().data().clear(options().context(), node -> node instanceof final SuffixNode suffix && suffix.getPriority() == priority);
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public <T> Optional<T> getInfoNode(final String key, final Function<String, @Nullable T> mapper) {
        return user().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(final String key, final String value) {
        final var result = user().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(final String key) {
        user().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return true;
    }

    @Override
    public boolean removeInfoNode(final String key, final String value) {
        final var result = user().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getUserManager().saveUser(user());
        return result.wasSuccessful();
    }
}
