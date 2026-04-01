package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

public final class PermissionTestSuite extends TestSuite<PermissionController> {
    public PermissionTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final PermissionController controller) {
        super(plugin, source, controller);
    }

    @Test(order = 1)
    private void testLoadPermissionHolder(final Player player) {
        controller.loadPermissionHolder(player).thenAccept(holder -> {
            pass("loadPermissionHolder", "loaded permission holder for " + player.getName());

            assertGetPermissions(holder);
            assertAddPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.TRUE);
            assertSetPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.FALSE);
            assertRemovePermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.NOT_SET);
        }).exceptionally(throwable -> {
            fail("loadPermissionHolder", throwable.getMessage());
            return null;
        });
    }

    @Test(order = 2)
    private void testGetPermissionHolder(final Player player) {
        final var holder = controller.getPermissionHolder(player);
        if (holder.isPresent()) pass("getPermissionHolder", "found cached permission holder");
        else fail("getPermissionHolder", "permission holder not cached after load");
    }

    @Test(order = 3)
    private void testResolvePermissionHolder(final Player player) {
        controller.resolvePermissionHolder(player).thenAccept(holder ->
                pass("resolvePermissionHolder", "resolved permission holder for " + player.getName())
        ).exceptionally(throwable -> {
            fail("resolvePermissionHolder", throwable.getMessage());
            return null;
        });
    }

    private void assertGetPermissions(final PermissionHolder holder) {
        final var permissions = holder.getPermissions();
        pass("getPermissions", permissions.size() + " permission(s)");
    }

    private void assertAddPermission(final PermissionHolder holder) {
        final var added = holder.addPermission("service.io.test");
        if (added) pass("addPermission", "added 'service.io.test'");
        else fail("addPermission", "failed to add permission");
    }

    private void assertCheckPermission(final PermissionHolder holder, final String permission, final TriState expected) {
        final var state = holder.checkPermission(permission);
        if (state == expected) pass("checkPermission('" + permission + "')", state.toString());
        else fail("checkPermission('" + permission + "')", "expected " + expected + " but got " + state);
    }

    private void assertSetPermission(final PermissionHolder holder) {
        final var set = holder.setPermission("service.io.test", false);
        if (set) pass("setPermission", "set 'service.io.test' to false");
        else fail("setPermission", "failed to set permission");
    }

    private void assertRemovePermission(final PermissionHolder holder) {
        final var removed = holder.removePermission("service.io.test");
        if (removed) pass("removePermission", "removed 'service.io.test'");
        else fail("removePermission", "failed to remove permission");
    }
}
