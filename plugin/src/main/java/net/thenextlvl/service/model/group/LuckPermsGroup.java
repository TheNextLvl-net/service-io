package net.thenextlvl.service.model.group;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.node.types.WeightNode;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.group.Group;
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
    public Optional<String> getPrefix(int priority) {
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
    public Optional<String> getSuffix(int priority) {
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
    public boolean setDisplayName(@Nullable String displayName) {
        if (displayName == null) {
            group.data().clear(DisplayNameNode.class::isInstance);
            return true;
        }
        var result = group().data().add(DisplayNameNode.builder(displayName).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setWeight(int weight) {
        var result = group().data().add(WeightNode.builder(weight).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setPrefix(@Nullable String prefix, int priority) {
        if (prefix == null) {
            group.data().clear(PrefixNode.class::isInstance);
            return true;
        }
        var result = group().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setSuffix(@Nullable String suffix, int priority) {
        if (suffix == null) {
            group.data().clear(SuffixNode.class::isInstance);
            return true;
        }
        var result = group().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return group().getCachedData().getPermissionData().getPermissionMap();
    }

    @Override
    public TriState checkPermission(String permission) {
        return switch (group().getCachedData().getPermissionData(options()).checkPermission(permission)) {
            case FALSE -> TriState.FALSE;
            case TRUE -> TriState.TRUE;
            case UNDEFINED -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        return setPermission(permission, true);
    }

    @Override
    public boolean removePermission(String permission) {
        var result = group().data().remove(Node.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setPermission(String permission, boolean value) {
        var result = group().data().add(Node.builder(permission).value(value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<String, T> mapper) {
        return group().getCachedData().getMetaData(options()).getMetaValue(key, mapper);
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        var result = group().data().add(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean removeInfoNode(String key) {
        group().data().clear(options().context(), node -> node.getKey().equals(key));
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return true;
    }

    @Override
    public boolean removeInfoNode(String key, String value) {
        var result = group().data().remove(MetaNode.builder(key, value).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }
}
