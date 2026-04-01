package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

public final class GroupTestSuite extends TestSuite<GroupController> {
    public GroupTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final GroupController controller) {
        super(plugin, source, controller);
    }

    @Test(order = 1)
    private void testGetGroups() {
        final var groups = controller.getGroups();
        pass("getGroups", groups.size() + " group(s) cached");
    }

    @Test(order = 2)
    private void testGroupLifecycle() {
        final var name = "service-io-test";

        controller.createGroup(name).thenAccept(group -> {
            pass("createGroup", "created group '" + group.getName() + "'");

            assertGroupFound(name);
            assertGetGroups();
            assertGetName(group);
            assertGetWeight(group);
            assertSetWeight(group);

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

    @Test(order = 3)
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

    private void assertGetWeight(final Group group) {
        final var weight = group.getWeight();
        pass("getWeight", weight.isPresent() ? String.valueOf(weight.getAsInt()) : "(not set)");
    }

    private void assertSetWeight(final Group group) {
        final var set = group.setWeight(42);
        if (set) {
            pass("setWeight", "set weight to 42");
            group.setWeight(0);
        } else {
            fail("setWeight", "failed to set weight");
        }
    }
}
