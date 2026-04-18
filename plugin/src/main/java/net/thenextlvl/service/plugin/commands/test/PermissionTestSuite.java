package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public final class PermissionTestSuite extends TestSuite<PermissionController> {
    public PermissionTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final PermissionController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        playerAsyncTest("loadPermissionHolder", this::testLoadPermissionHolder);
        playerTest("getPermissionHolder", this::testGetPermissionHolder);
        playerAsyncTest("resolvePermissionHolder", this::testResolvePermissionHolder);
    }

    private CompletableFuture<Void> testLoadPermissionHolder(final Player player) {
        return controller.loadPermissionHolder(player).thenAccept(holder -> {
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
        });
    }

    private void testGetPermissionHolder(final Player player) {
        final var holder = controller.getPermissionHolder(player);
        if (holder.isPresent()) pass("getPermissionHolder", "found cached permission holder");
        else fail("getPermissionHolder", "permission holder not cached after load");
    }

    private CompletableFuture<Void> testResolvePermissionHolder(final Player player) {
        return controller.resolvePermissionHolder(player).thenAccept(holder ->
                pass("resolvePermissionHolder", "resolved permission holder for " + player.getName()));
    }

    private void assertGetPermissions(final PermissionHolder holder) {
        final var permissions = holder.getPermissions();
        pass("getPermissions", permissions.size() + " permission(s)");
    }

    private void assertAddPermission(final PermissionHolder holder) {
        final var added = holder.addPermission("service.io.test");
        final var state = holder.checkPermission("service.io.test");
        assertRequiredStateChange("addPermission", added, state == TriState.TRUE,
                "added 'service.io.test'",
                "failed to add permission",
                "addPermission returned true but checkPermission returned " + state + " instead of TRUE");
    }

    private void assertCheckPermission(final PermissionHolder holder, final String permission, final TriState expected) {
        final var state = holder.checkPermission(permission);
        if (state == expected) pass("checkPermission('" + permission + "')", state.toString());
        else fail("checkPermission('" + permission + "')", "expected " + expected + " but got " + state);
    }

    private void assertSetPermission(final PermissionHolder holder) {
        final var set = holder.setPermission("service.io.test", false);
        final var state = holder.checkPermission("service.io.test");
        assertRequiredStateChange("setPermission", set, state == TriState.FALSE,
                "set 'service.io.test' to false",
                "failed to set permission",
                "setPermission returned true but checkPermission returned " + state + " instead of FALSE");
    }

    private void assertRemovePermission(final PermissionHolder holder) {
        final var removed = holder.removePermission("service.io.test");
        final var state = holder.checkPermission("service.io.test");
        assertRequiredStateChange("removePermission", removed, state == TriState.NOT_SET,
                "removed 'service.io.test'",
                "failed to remove permission",
                "removePermission returned true but checkPermission returned " + state + " instead of NOT_SET");
    }

    private void assertSetInfoNode(final PermissionHolder holder) {
        final var set = holder.setInfoNode("service.io.test", "hello");
        final var has = holder.hasInfoNode("service.io.test");
        final var value = holder.getInfoNode("service.io.test");
        assertRequiredStateChange("setInfoNode", set, has && value.isPresent() && "hello".equals(value.get()),
                "set 'service.io.test' to 'hello'",
                "failed to set info node",
                !has ? "setInfoNode returned true but hasInfoNode returned false"
                        : "setInfoNode returned true but getInfoNode returned " + value + " instead of Optional[hello]");
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
        final var has = holder.hasInfoNode("service.io.test");
        assertRequiredStateChange("removeInfoNode", removed, !has,
                "removed 'service.io.test'",
                "failed to remove info node",
                "removeInfoNode returned true but hasInfoNode returned true");
    }
}
