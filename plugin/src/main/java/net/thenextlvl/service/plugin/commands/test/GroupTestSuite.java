package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.group.GroupHolder;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public final class GroupTestSuite extends TestSuite<GroupController> {
    public GroupTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final GroupController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getGroups", this::testGetGroups);
        asyncTest("loadGroups", this::testLoadGroups);
        asyncTest("groupLifecycle", this::testGroupLifecycle);
        playerAsyncTest("groupHolder", this::testGroupHolder);
    }

    private void testGetGroups() {
        final var groups = controller.getGroups();
        pass("getGroups", groups.size() + " group(s) cached");
    }

    private CompletableFuture<Void> testLoadGroups() {
        return controller.loadGroups().thenAccept(groups ->
                pass("loadGroups", groups.size() + " group(s) loaded"));
    }

    private CompletableFuture<Void> testGroupLifecycle() {
        final var name = "service-io-test";

        return controller.createGroup(name).thenCompose(group -> {
            pass("createGroup", "created group '" + group.getName() + "'");

            assertGroupFound(name);
            assertGetGroups();
            assertGetName(group);
            assertGetWorld(group);
            assertGetWeight(group);
            assertSetWeight(group);
            assertDisplayName(group);
            assertPrefix(group);
            assertSuffix(group);
            assertPrefixes(group);
            assertSuffixes(group);
            assertPermissions(group);
            assertAddPermission(group);
            assertCheckPermission(group, "service.io.test", TriState.TRUE);
            assertSetPermission(group);
            assertCheckPermission(group, "service.io.test", TriState.FALSE);
            assertRemovePermission(group);
            assertCheckPermission(group, "service.io.test", TriState.NOT_SET);
            assertInfoNode(group);
            return assertLoadGroup(name)
                    .thenCompose(ignored -> assertResolveGroup(name))
                    .thenCompose(ignored -> controller.deleteGroup(name).thenAccept(deleted -> {
                        if (deleted) pass("deleteGroup", "deleted group '" + name + "'");
                        else fail("deleteGroup", "failed to delete group");

                        assertGroupNotFound(name);
                    }));
        });
    }

    private CompletableFuture<Void> testGroupHolder(final Player player) {
        return controller.loadGroupHolder(player).thenCompose(holder -> {
            pass("loadGroupHolder", "loaded group holder for " + player.getName());

            final var groups = holder.getGroups();
            pass("getGroups (holder)", groups.size() + " group(s)");

            if (!groups.isEmpty()) {
                final var primaryGroup = holder.getPrimaryGroup();
                pass("getPrimaryGroup", primaryGroup);
            } else {
                pass("getPrimaryGroup", "no groups assigned");
            }

            assertHolderAddGroup(holder);
            assertHolderInGroup(holder, "service-io-test", true);
            assertHolderSetPrimaryGroup(holder);
            assertHolderRemoveGroup(holder);
            assertHolderInGroup(holder, "service-io-test", false);

            assertHolderPermissions(holder);
            assertAddPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.TRUE);
            assertSetPermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.FALSE);
            assertRemovePermission(holder);
            assertCheckPermission(holder, "service.io.test", TriState.NOT_SET);
            assertInfoNode(holder);

            assertGetGroupHolder(player);
            return assertResolveGroupHolder(player);
        });
    }

    private void assertGroupFound(final String name) {
        final var group = controller.getGroup(name);
        if (group.isPresent()) pass("getGroup", "found '" + name + "'");
        else fail("getGroup", "group '" + name + "' not found after creation");
    }

    private void assertGroupNotFound(final String name) {
        final var group = controller.getGroup(name);
        if (group.isEmpty()) pass("getGroup (after delete)", "group no longer found");
        else fail("getGroup (after delete)", "group still found after deletion");
    }

    private void assertGetGroups() {
        final var groups = controller.getGroups();
        pass("getGroups", groups.size() + " group(s) cached");
    }

    private void assertGetName(final Group group) {
        pass("getName", group.getName());
    }

    private void assertGetWorld(final Group group) {
        pass("getWorld", group.getWorld().map(world -> world.key().asString()).orElse("(not set)"));
    }

    private void assertGetWeight(final Group group) {
        pass("getWeight", group.getWeight().map(String::valueOf).orElse("(not set)"));
    }

    private void assertSetWeight(final Group group) {
        final var before = group.getWeight();
        final var set = group.setWeight(42);
        final var after = group.getWeight();
        assertChangedValue("setWeight", set, before.orElse(null), after.orElse(null), 42, "set weight to 42");
        before.ifPresentOrElse(group::setWeight, () -> group.setWeight(0));
    }

    private void assertDisplayName(final Group group) {
        final var displayName = group.getDisplayName();
        pass("getDisplayName", displayName.orElse("(not set)"));

        final var set = group.setDisplayName("TestDisplay");
        final var updated = group.getDisplayName().orElse(null);
        assertChangedValue("setDisplayName", set, displayName.orElse(null), updated, "TestDisplay", "set to 'TestDisplay'");
        group.setDisplayName(displayName.orElse(null));
    }

    private void assertPrefix(final Group group) {
        final var prefix = group.getPrefix();
        pass("getPrefix", prefix.orElse("(not set)"));

        final var set = group.setPrefix("TestPrefix");
        final var updated = group.getPrefix().orElse(null);
        assertChangedValue("setPrefix", set, prefix.orElse(null), updated, "TestPrefix", "set to 'TestPrefix'");
        group.setPrefix(prefix.orElse(null));
    }

    private void assertSuffix(final Group group) {
        final var suffix = group.getSuffix();
        pass("getSuffix", suffix.orElse("(not set)"));

        final var set = group.setSuffix("TestSuffix");
        final var updated = group.getSuffix().orElse(null);
        assertChangedValue("setSuffix", set, suffix.orElse(null), updated, "TestSuffix", "set to 'TestSuffix'");
        group.setSuffix(suffix.orElse(null));
    }

    private void assertPrefixes(final Group group) {
        final var prefixes = group.getPrefixes();
        pass("getPrefixes", prefixes.size() + " prefix(es)");
    }

    private void assertSuffixes(final Group group) {
        final var suffixes = group.getSuffixes();
        pass("getSuffixes", suffixes.size() + " suffix(es)");
    }

    private void assertPermissions(final PermissionHolder holder) {
        final var permissions = holder.getPermissions();
        pass("getPermissions", permissions.size() + " permission(s)");
    }

    private void assertHolderPermissions(final GroupHolder holder) {
        final var permissions = holder.getPermissions();
        pass("getPermissions (holder)", permissions.size() + " permission(s)");
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

    private void assertInfoNode(final PermissionHolder holder) {
        final var set = holder.setInfoNode("service.io.key", "testValue");
        final var has = holder.hasInfoNode("service.io.key");
        final var value = holder.getInfoNode("service.io.key");
        assertRequiredStateChange("setInfoNode", set, has && value.isPresent() && "testValue".equals(value.get()),
                "set 'service.io.key' to 'testValue'",
                "failed to set info node",
                !has ? "'service.io.key' not found after set"
                        : "expected 'testValue' but got " + value.orElse("(not set)"));
        assertState("hasInfoNode", has, "'service.io.key' exists", "'service.io.key' not found after set");
        assertState("getInfoNode", value.isPresent() && "testValue".equals(value.get()),
                "value is 'testValue'",
                "expected 'testValue' but got " + value.orElse("(not set)"));

        final var removed = holder.removeInfoNode("service.io.key");
        final var hasAfter = holder.hasInfoNode("service.io.key");
        assertRequiredStateChange("removeInfoNode", removed, !hasAfter,
                "removed 'service.io.key'",
                "failed to remove info node",
                "'service.io.key' still exists after removal");
        assertState("hasInfoNode (after remove)", !hasAfter,
                "'service.io.key' no longer exists",
                "'service.io.key' still exists after removal");
    }

    private CompletableFuture<Void> assertLoadGroup(final String name) {
        return controller.loadGroup(name).thenAccept(group ->
                pass("loadGroup", "loaded group '" + group.getName() + "'"));
    }

    private CompletableFuture<Void> assertResolveGroup(final String name) {
        return controller.resolveGroup(name).thenAccept(group ->
                pass("resolveGroup", "resolved group '" + group.getName() + "'"));
    }

    private void assertGetGroupHolder(final Player player) {
        final var holder = controller.getGroupHolder(player);
        if (holder.isPresent()) pass("getGroupHolder", "found cached group holder");
        else fail("getGroupHolder", "group holder not cached after load");
    }

    private CompletableFuture<Void> assertResolveGroupHolder(final Player player) {
        return controller.resolveGroupHolder(player).thenAccept(holder ->
                pass("resolveGroupHolder", "resolved group holder for " + player.getName()));
    }

    private void assertHolderAddGroup(final GroupHolder holder) {
        final var added = holder.addGroup("service-io-test");
        final var inGroup = holder.inGroup("service-io-test");
        assertRequiredStateChange("addGroup", added, inGroup,
                "added and verified 'service-io-test'",
                "failed to add group",
                "addGroup returned true but inGroup is false");
    }

    private void assertHolderInGroup(final GroupHolder holder, final String name, final boolean expected) {
        final var inGroup = holder.inGroup(name);
        if (inGroup == expected) pass("inGroup('" + name + "')", String.valueOf(inGroup));
        else fail("inGroup('" + name + "')", "expected " + expected + " but got " + inGroup);
    }

    private void assertHolderSetPrimaryGroup(final GroupHolder holder) {
        final var before = holder.getPrimaryGroup();
        final var set = holder.setPrimaryGroup("service-io-test");
        final var after = holder.getPrimaryGroup();
        assertChangedValue("setPrimaryGroup", set, before, after, "service-io-test", "set to 'service-io-test'");
        holder.setPrimaryGroup(before);
    }

    private void assertHolderRemoveGroup(final GroupHolder holder) {
        final var removed = holder.removeGroup("service-io-test");
        final var inGroup = holder.inGroup("service-io-test");
        assertRequiredStateChange("removeGroup", removed, !inGroup,
                "removed and verified 'service-io-test'",
                "failed to remove group",
                "removeGroup returned true but inGroup is still true");
    }
}
