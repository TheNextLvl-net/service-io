package net.thenextlvl.service.providers.superperms;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.permission.PermissionHolder;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
final class NoOpHolder implements PermissionHolder {
    public static final NoOpHolder INSTANCE = new NoOpHolder();

    private NoOpHolder() {
    }

    @Override
    public @Unmodifiable Map<String, Boolean> getPermissions() {
        return Map.of();
    }

    @Override
    public TriState checkPermission(final String permission) {
        return TriState.FALSE;
    }

    @Override
    public boolean addPermission(final String permission) {
        return false;
    }

    @Override
    public boolean removePermission(final String permission) {
        return false;
    }

    @Override
    public boolean setPermission(final String permission, final boolean value) {
        return false;
    }
}
