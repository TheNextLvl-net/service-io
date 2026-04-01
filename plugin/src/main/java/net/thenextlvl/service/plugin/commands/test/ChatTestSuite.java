package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.chat.ChatProfile;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

public final class ChatTestSuite extends TestSuite<ChatController> {
    public ChatTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final ChatController controller) {
        super(plugin, source, controller);
    }

    @Test(order = 1)
    private void testLoadProfile(final Player player) {
        controller.loadProfile(player).thenAccept(profile -> {
            pass("loadProfile", "loaded profile for " + player.getName());

            assertGetName(profile);
            assertGetPrimaryGroup(profile);
            assertGetGroups(profile);
            assertGetPrefix(profile);
            assertGetSuffix(profile);
            assertGetDisplayName(profile);
        }).exceptionally(throwable -> {
            fail("loadProfile", throwable.getMessage());
            return null;
        });
    }

    @Test(order = 2)
    private void testGetProfile(final Player player) {
        final var profile = controller.getProfile(player);
        if (profile.isPresent()) pass("getProfile", "found cached profile");
        else fail("getProfile", "profile not cached after load");
    }

    @Test(order = 3)
    private void testResolveProfile(final Player player) {
        controller.resolveProfile(player).thenAccept(profile ->
                pass("resolveProfile", "resolved profile for " + player.getName())
        ).exceptionally(throwable -> {
            fail("resolveProfile", throwable.getMessage());
            return null;
        });
    }

    private void assertGetName(final ChatProfile profile) {
        final var name = profile.getName();
        pass("getName", name.orElse("(not set)"));
    }

    private void assertGetPrimaryGroup(final ChatProfile profile) {
        final var group = profile.getPrimaryGroup();
        pass("getPrimaryGroup", group.orElse("(not set)"));
    }

    private void assertGetGroups(final ChatProfile profile) {
        final var groups = profile.getGroups();
        pass("getGroups", groups.size() + " group(s)");
    }

    private void assertGetPrefix(final ChatProfile profile) {
        final var prefix = profile.getPrefix();
        pass("getPrefix", prefix.orElse("(not set)"));
    }

    private void assertGetSuffix(final ChatProfile profile) {
        final var suffix = profile.getSuffix();
        pass("getSuffix", suffix.orElse("(not set)"));
    }

    private void assertGetDisplayName(final ChatProfile profile) {
        final var displayName = profile.getDisplayName();
        pass("getDisplayName", displayName.orElse("(not set)"));
    }
}
