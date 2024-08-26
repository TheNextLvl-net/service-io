package net.thenextlvl.service.model.group;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public record GroupManagerGroup(org.anjocaido.groupmanager.data.Group group) implements Group {

    @Override
    public Optional<String> getDisplayName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPrefix() {
        return getInfoNode("prefix");
    }

    @Override
    public Optional<String> getSuffix() {
        return getInfoNode("suffix");
    }

    @Override
    public OptionalInt getWeight() {
        return OptionalInt.empty();
    }

    @Override
    public String getName() {
        return group().getName();
    }

    @Override
    public boolean setDisplayName(String displayName) {
        return false;
    }

    @Override
    public boolean setWeight(int weight) {
        return false;
    }

    @Override
    public boolean setPrefix(String prefix, int priority) {
        return setInfoNode("prefix", prefix);
    }

    @Override
    public boolean setSuffix(String suffix, int priority) {
        return setInfoNode("suffix", suffix);
    }

    @Override
    public TriState checkPermission(String permission) {
        var handler = group().getDataSource().getPermissionsHandler();
        return switch(handler.checkGroupOnlyPermission(group(), permission).resultType) {
            case FOUND -> TriState.TRUE;
            case NEGATION, EXCEPTION -> TriState.FALSE;
            default -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        if (!checkPermission(permission).equals(TriState.NOT_SET)) return false;
        group().addPermission(permission);
        return true;
    }

    @Override
    public boolean removePermission(String permission) {
        return group().removePermission(permission);
    }

    @Override
    public <T> Optional<T> getInfoNode(String key, Function<@Nullable String, @Nullable T> mapper) {
        return Optional.ofNullable(mapper.apply(group().getVariables().getVarString(key)));
    }

    @Override
    public boolean removeInfoNode(String key) {
        if (!hasInfoNode(key)) return false;
        group().getVariables().removeVar(key);
        return true;
    }

    @Override
    public boolean removeInfoNode(String key, String value) {
        var infoNode = getInfoNode(key).orElse(null);
        if (!value.equals(infoNode)) return false;
        return removeInfoNode(key);
    }

    @Override
    public boolean setInfoNode(String key, String value) {
        group().getVariables().addVar(key, value);
        return true;
    }

    @Override
    public boolean hasInfoNode(String key) {
        return group().getVariables().hasVar(key);
    }
}
