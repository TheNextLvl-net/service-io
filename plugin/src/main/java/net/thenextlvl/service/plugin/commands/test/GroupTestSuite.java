package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.group.GroupHolder;
import net.thenextlvl.service.permission.PermissionHolder;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

public final class GroupTestSuite extends TestSuite<GroupController> {
    public GroupTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final GroupController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getGroups", this::testGetGroups);
        test("loadGroups", this::testLoadGroups);
        test("groupLifecycle", this::testGroupLifecycle);
        playerTest("groupHolder", this::testGroupHolder);
    }

    private void testGetGroups() {
        final var groups = controller.getGroups();
        pass("getGroups", groups.size() + " group(s) cached");
    }

    private void testLoadGroups() {
        controller.loadGroups().thenAccept(groups ->
                pass("loadGroups", groups.size() + " group(s) loaded")
        ).exceptionally(throwable -> {
            fail("loadGroups", throwable.getMessage());
            return null;
        });
    }

    private void testGroupLifecycle() {
        final var name = "service-io-test";

        controller.createGroup(name).thenAccept(group -> {
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
            assertLoadGroup(name);
            assertResolveGroup(name);

            controller.deleteGroup(name).thenAccept(deleted -> {
                if (deleted) pass("deleteGroup", "deleted group '" + name + "'");
                else fail("deleteGroup", "failed to delete group");

                assertGroupNotFound(name);
            }).exceptionally(throwable -> {
                fail("deleteGroup", throwable.getMessage());
                return null;
            });
        }).exceptionally(throwable -> {
            fail("createGroup", throwable.getMessage());
            return null;
        });
    }

    private void testGroupHolder(final Player player) {
        controller.loadGroupHolder(player).thenAccept(holder -> {
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
            assertResolveGroupHolder(player);
        }).exceptionally(throwable -> {
            fail("loadGroupHolder", throwable.getMessage());
            return null;
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
        final var world = group.getWorld();
        pass("getWorld", world.map(WorldInfo::getName).orElse("(not set)"));
    }

    private void assertGetWeight(final Group group) {
        final var weight = group.getWeight();
        pass("getWeight", weight.isPresent() ? String.valueOf(weight.getAsInt()) : "(not set)");
    }

    private void assertSetWeight(final Group group) {
        final var before = group.getWeight();
        final var set = group.setWeight(42);
        final var after = group.getWeight();
        if (set) {
            if (after.isPresent() && after.getAsInt() == 42) pass("setWeight", "set weight to 42");
            else fail("setWeight", "returned true but value didn't change");
        } else {
            fail("setWeight", "returned false");
        }
        before.ifPresentOrElse(group::setWeight, () -> group.setWeight(0));
    }

    private void assertDisplayName(final Group group) {
        final var displayName = group.getDisplayName();
        pass("getDisplayName", displayName.orElse("(not set)"));

        final var set = group.setDisplayName("TestDisplay");
        if (set) {
            final var updated = group.getDisplayName();
            if (updated.isPresent() && "TestDisplay".equals(updated.get()))
                pass("setDisplayName", "set to 'TestDisplay'");
            else fail("setDisplayName", "returned true but value didn't change");
            group.setDisplayName(displayName.orElse(null));
        } else {
            fail("setDisplayName", "returned false");
        }
    }

    private void assertPrefix(final Group group) {
        final var prefix = group.getPrefix();
        pass("getPrefix", prefix.orElse("(not set)"));

        final var set = group.setPrefix("TestPrefix");
        if (set) {
            final var updated = group.getPrefix();
            if (updated.isPresent() && "TestPrefix".equals(updated.get())) pass("setPrefix", "set to 'TestPrefix'");
            else fail("setPrefix", "returned true but value didn't change");
            group.setPrefix(prefix.orElse(null));
        } else {
            fail("setPrefix", "returned false");
        }
    }

    private void assertSuffix(final Group group) {
        final var suffix = group.getSuffix();
        pass("getSuffix", suffix.orElse("(not set)"));

        final var set = group.setSuffix("TestSuffix");
        if (set) {
            final var updated = group.getSuffix();
            if (updated.isPresent() && "TestSuffix".equals(updated.get())) pass("setSuffix", "set to 'TestSuffix'");
            else fail("setSuffix", "returned true but value didn't change");
            group.setSuffix(suffix.orElse(null));
        } else {
            fail("setSuffix", "returned false");
        }
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

    private void assertInfoNode(final PermissionHolder holder) {
        final var set = holder.setInfoNode("service.io.key", "testValue");
        if (set) pass("setInfoNode", "set 'service.io.key' to 'testValue'");
        else fail("setInfoNode", "failed to set info node");

        final var has = holder.hasInfoNode("service.io.key");
        if (has) pass("hasInfoNode", "'service.io.key' exists");
        else fail("hasInfoNode", "'service.io.key' not found after set");

        final var value = holder.getInfoNode("service.io.key");
        if (value.isPresent() && "testValue".equals(value.get())) pass("getInfoNode", "value is 'testValue'");
        else fail("getInfoNode", "expected 'testValue' but got " + value.orElse("(not set)"));

        final var removed = holder.removeInfoNode("service.io.key");
        if (removed) pass("removeInfoNode", "removed 'service.io.key'");
        else fail("removeInfoNode", "failed to remove info node");

        final var hasAfter = holder.hasInfoNode("service.io.key");
        if (!hasAfter) pass("hasInfoNode (after remove)", "'service.io.key' no longer exists");
        else fail("hasInfoNode (after remove)", "'service.io.key' still exists after removal");
    }

    private void assertLoadGroup(final String name) {
        controller.loadGroup(name).thenAccept(group ->
                pass("loadGroup", "loaded group '" + group.getName() + "'")
        ).exceptionally(throwable -> {
            fail("loadGroup", throwable.getMessage());
            return null;
        });
    }

    private void assertResolveGroup(final String name) {
        controller.resolveGroup(name).thenAccept(group ->
                pass("resolveGroup", "resolved group '" + group.getName() + "'")
        ).exceptionally(throwable -> {
            fail("resolveGroup", throwable.getMessage());
            return null;
        });
    }

    private void assertGetGroupHolder(final Player player) {
        final var holder = controller.getGroupHolder(player);
        if (holder.isPresent()) pass("getGroupHolder", "found cached group holder");
        else fail("getGroupHolder", "group holder not cached after load");
    }

    private void assertResolveGroupHolder(final Player player) {
        controller.resolveGroupHolder(player).thenAccept(holder ->
                pass("resolveGroupHolder", "resolved group holder for " + player.getName())
        ).exceptionally(throwable -> {
            fail("resolveGroupHolder", throwable.getMessage());
            return null;
        });
    }

    private void assertHolderAddGroup(final GroupHolder holder) {
        final var added = holder.addGroup("service-io-test");
        if (added) {
            final var inGroup = holder.inGroup("service-io-test");
            if (inGroup) pass("addGroup", "added and verified 'service-io-test'");
            else fail("addGroup", "returned true but inGroup is false");
        } else {
            fail("addGroup", "failed to add group");
        }
    }

    private void assertHolderInGroup(final GroupHolder holder, final String name, final boolean expected) {
        final var inGroup = holder.inGroup(name);
        if (inGroup == expected) pass("inGroup('" + name + "')", String.valueOf(inGroup));
        else fail("inGroup('" + name + "')", "expected " + expected + " but got " + inGroup);
    }

    private void assertHolderSetPrimaryGroup(final GroupHolder holder) {
        final var before = holder.getPrimaryGroup();
        final var set = holder.setPrimaryGroup("service-io-test");
        if (set) {
            final var after = holder.getPrimaryGroup();
            if ("service-io-test".equals(after)) pass("setPrimaryGroup", "set to 'service-io-test'");
            else fail("setPrimaryGroup", "returned true but getPrimaryGroup is '" + after + "'");
            holder.setPrimaryGroup(before);
        } else {
            fail("setPrimaryGroup", "returned false");
        }
    }

    private void assertHolderRemoveGroup(final GroupHolder holder) {
        final var removed = holder.removeGroup("service-io-test");
        if (removed) {
            final var inGroup = holder.inGroup("service-io-test");
            if (!inGroup) pass("removeGroup", "removed and verified 'service-io-test'");
            else fail("removeGroup", "returned true but inGroup is still true");
        } else {
            fail("removeGroup", "failed to remove group");
        }
    }
}
