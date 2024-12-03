package net.thenextlvl.service.model.group;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.api.group.Group;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public record GroupManagerGroup(org.anjocaido.groupmanager.data.Group group) implements Group {
    @Override
    public Optional<String> getDisplayName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPrefix(int priority) {
        return getInfoNode("prefix");
    }

    @Override
    public @Unmodifiable Map<Integer, String> getPrefixes() {
        return getPrefix().map(prefix -> Map.of(0, prefix)).orElseGet(Map::of);
    }

    @Override
    public Optional<String> getSuffix(int priority) {
        return getInfoNode("suffix");
    }

    @Override
    public @Unmodifiable Map<Integer, String> getSuffixes() {
        return getSuffix().map(suffix -> Map.of(0, suffix)).orElseGet(Map::of);
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
    public Optional<World> getWorld() {
        var plugin = JavaPlugin.getPlugin(GroupManager.class);
        return Optional.ofNullable(group().getDataSource().getName())
                .map(plugin.getServer()::getWorld);
    }

    @Override
    public boolean setDisplayName(@Nullable String displayName) {
        return false;
    }

    @Override
    public boolean setWeight(int weight) {
        return false;
    }

    @Override
    public boolean setPrefix(@Nullable String prefix, int priority) {
        return setInfoNode("prefix", prefix);
    }

    @Override
    public boolean setSuffix(@Nullable String suffix, int priority) {
        return setInfoNode("suffix", suffix);
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return group.getPermissionList().stream().collect(Collectors.toUnmodifiableMap(
                permission -> permission, permission -> checkPermission(permission).toBooleanOrElse(false))
        );
    }

    @Override
    public TriState checkPermission(String permission) {
        var handler = group().getDataSource().getPermissionsHandler();
        return switch (handler.checkGroupOnlyPermission(group(), permission).resultType) {
            case FOUND -> TriState.TRUE;
            case NEGATION, EXCEPTION -> TriState.FALSE;
            default -> TriState.NOT_SET;
        };
    }

    @Override
    public boolean addPermission(String permission) {
        return setPermission(permission, true);
    }

    @Override
    public boolean removePermission(String permission) {
        return group().removePermission(permission);
    }

    @Override
    public boolean setPermission(String permission, boolean value) {
        var state = checkPermission(permission).toBoolean();
        if (state != null && state.equals(value)) return false;
        removePermission(value ? "-" + permission : permission);
        group().addPermission(!value ? "-" + permission : permission);
        return true;
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
    public boolean setInfoNode(String key, @Nullable String value) {
        group().getVariables().addVar(key, value);
        return true;
    }

    @Override
    public boolean hasInfoNode(String key) {
        return group().getVariables().hasVar(key);
    }
}
