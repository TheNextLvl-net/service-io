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

    @Override
    protected void setup() {
        playerTest("loadPermissionHolder", this::testLoadPermissionHolder);
        playerTest("getPermissionHolder", this::testGetPermissionHolder);
        playerTest("resolvePermissionHolder", this::testResolvePermissionHolder);
    }

    private void testLoadPermissionHolder(final Player player) {
        controller.loadPermissionHolder(player).thenAccept(holder -> {
            pass("loadPermissionHolder", "loaded permission holder for " + player.getName());

            assertGetPermissions(holder);
            assertCheckPermission(holder, "service.io.test", TriState.NOT_SET);
            assertAddPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.TRUE);
            assertSetPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.FALSE);
            assertRemovePermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.NOT_SET);

            assertSetInfoNode(holder);
            assertBooleanInfoNode(holder);
            assertIntInfoNode(holder);
            assertDoubleInfoNode(holder);
            assertRemoveInfoNode(holder);
        }).exceptionally(throwable -> {
            fail("loadPermissionHolder", throwable.getMessage());
            return null;
        });
    }

    private void testGetPermissionHolder(final Player player) {
        final var holder = controller.getPermissionHolder(player);
        if (holder.isPresent()) pass("getPermissionHolder", "found cached permission holder");
        else fail("getPermissionHolder", "permission holder not cached after load");
    }

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
        if (added) {
            final var state = holder.checkPermission("service.io.test");
            if (state == TriState.TRUE) pass("addPermission", "added 'service.io.test'");
            else fail("addPermission", "added but checkPermission returned " + state + " instead of TRUE");
        } else fail("addPermission", "failed to add permission");
    }

    private void assertCheckPermission(final PermissionHolder holder, final String permission, final TriState expected) {
        final var state = holder.checkPermission(permission);
        if (state == expected) pass("checkPermission('" + permission + "')", state.toString());
        else fail("checkPermission('" + permission + "')", "expected " + expected + " but got " + state);
    }

    private void assertSetPermission(final PermissionHolder holder) {
        final var set = holder.setPermission("service.io.test", false);
        if (set) {
            final var state = holder.checkPermission("service.io.test");
            if (state == TriState.FALSE) pass("setPermission", "set 'service.io.test' to false");
            else fail("setPermission", "set but checkPermission returned " + state + " instead of FALSE");
        } else fail("setPermission", "failed to set permission");
    }

    private void assertRemovePermission(final PermissionHolder holder) {
        final var removed = holder.removePermission("service.io.test");
        if (removed) {
            final var state = holder.checkPermission("service.io.test");
            if (state == TriState.NOT_SET) pass("removePermission", "removed 'service.io.test'");
            else fail("removePermission", "removed but checkPermission returned " + state + " instead of NOT_SET");
        } else fail("removePermission", "failed to remove permission");
    }

    private void assertSetInfoNode(final PermissionHolder holder) {
        final var set = holder.setInfoNode("service.io.test", "hello");
        if (set) {
            if (!holder.hasInfoNode("service.io.test")) {
                fail("setInfoNode", "set but hasInfoNode returned false");
                return;
            }
            final var value = holder.getInfoNode("service.io.test");
            if (value.isPresent() && "hello".equals(value.get())) pass("setInfoNode", "set 'service.io.test' to 'hello'");
            else fail("setInfoNode", "set but getInfoNode returned " + value + " instead of Optional[hello]");
        } else fail("setInfoNode", "failed to set info node");
    }

    private void assertBooleanInfoNode(final PermissionHolder holder) {
        holder.setInfoNode("service.io.test", "true");
        final var value = holder.booleanInfoNode("service.io.test");
        if (value.isPresent() && value.get()) pass("booleanInfoNode", "returned true");
        else fail("booleanInfoNode", "expected Optional[true] but got " + value);
    }

    private void assertIntInfoNode(final PermissionHolder holder) {
        holder.setInfoNode("service.io.test", "42");
        final var value = holder.intInfoNode("service.io.test");
        if (value.isPresent() && value.get() == 42) pass("intInfoNode", "returned 42");
        else fail("intInfoNode", "expected Optional[42] but got " + value);
    }

    private void assertDoubleInfoNode(final PermissionHolder holder) {
        holder.setInfoNode("service.io.test", "3.14");
        final var value = holder.doubleInfoNode("service.io.test");
        if (value.isPresent() && value.get() == 3.14) pass("doubleInfoNode", "returned 3.14");
        else fail("doubleInfoNode", "expected Optional[3.14] but got " + value);
    }

    private void assertRemoveInfoNode(final PermissionHolder holder) {
        final var removed = holder.removeInfoNode("service.io.test");
        if (removed) {
            if (holder.hasInfoNode("service.io.test")) fail("removeInfoNode", "removed but hasInfoNode returned true");
            else pass("removeInfoNode", "removed 'service.io.test'");
        } else fail("removeInfoNode", "failed to remove info node");
    }
}
