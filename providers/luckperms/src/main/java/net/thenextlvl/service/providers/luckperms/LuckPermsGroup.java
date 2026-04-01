package net.thenextlvl.service.providers.luckperms;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.node.types.WeightNode;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.util.Tristate;
import net.thenextlvl.service.group.Group;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

@NullMarked
public record LuckPermsGroup(
        net.luckperms.api.model.group.Group group,
        QueryOptions options,
        @Nullable World world
) implements Group {
    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(group().getDisplayName(options()));
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(group().getCachedData().getMetaData(options()).getPrefix());
    }

    @Override
    public Optional<String> getPrefix(final int priority) {
        return Optional.ofNullable(getPrefixes().get(priority));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getPrefixes() {
        return group().getCachedData().getMetaData(options()).getPrefixes();
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(group().getCachedData().getMetaData(options()).getSuffix());
    }

    @Override
    public Optional<String> getSuffix(final int priority) {
        return Optional.ofNullable(getSuffixes().get(priority));
    }

    @Override
    public @Unmodifiable Map<Integer, String> getSuffixes() {
        return group().getCachedData().getMetaData(options()).getSuffixes();
    }

    @Override
    public OptionalInt getWeight() {
        return group().getWeight();
    }

    @Override
    public String getName() {
        return group().getName();
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.ofNullable(world());
    }

    @Override
    public boolean setDisplayName(@Nullable final String displayName) {
        if (displayName == null) {
            group.data().clear(node -> node instanceof DisplayNameNode);
            return true;
        }
        final var result = group().data().add(DisplayNameNode.builder(displayName).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setWeight(final int weight) {
        final var result = group().data().add(WeightNode.builder(weight).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setPrefix(@Nullable final String prefix, final int priority) {
        if (prefix == null) {
            group.data().clear(node -> node instanceof PrefixNode);
            return true;
        }
        final var result = group().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setSuffix(@Nullable final String suffix, final int priority) {
        if (suffix == null) {
            group.data().clear(node -> node instanceof SuffixNode);
            return true;
        }
        final var result = group().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return group().getCachedData().getPermissionData().getPermissionMap();
    }

    @Override
    public TriState checkPermission(final String permission) {
        return switch (group().getCachedData().getPermissionData(options()).checkPermission(permission)) {
            case Tristate.FALSE -> TriState.FALSE;
            case Tristate.TRUE -> TriState.TRUE;
            case Tristate.UNDEFINED -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(final String permission) {
        return setPermission(permission, true);
    }

    @Override
    public boolean removePermission(final String permission) {
        final var result = group().data().remove(Node.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setPermission(final String permission, final boolean value) {
        final var result = group().data().add(Node.builder(permission).value(value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public <T> Optional<T> getInfoNode(final String key, final Function<@Nullable String, @Nullable T> mapper) {
        return group().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(final String key, final String value) {
        final var result = group().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(final String key) {
        group().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return true;
    }

    @Override
    public boolean removeInfoNode(final String key, final String value) {
        final var result = group().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }
}
