package net.thenextlvl.service.model.group;

import net.kyori.adventure.util.TriState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.types.*;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.group.Group;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public record LuckPermsGroup(net.luckperms.api.model.group.Group group, QueryOptions options) implements Group {
    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(group().getDisplayName(options()));
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(group().getCachedData().getMetaData(options()).getPrefix());
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(group().getCachedData().getMetaData(options()).getSuffix());
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
    public boolean setDisplayName(String displayName) {
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
    public boolean setPrefix(String prefix, int priority) {
        var result = group().data().add(PrefixNode.builder(prefix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean setSuffix(String suffix, int priority) {
        var result = group().data().add(SuffixNode.builder(suffix, priority).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
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
        var result = group().data().add(PermissionNode.builder(permission).context(options().context()).build());
        LuckPermsProvider.get().getGroupManager().saveGroup(group());
        return result.wasSuccessful();
    }

    @Override
    public boolean removePermission(String permission) {
        var result = group().data().remove(PermissionNode.builder(permission).context(options().context()).build());
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
