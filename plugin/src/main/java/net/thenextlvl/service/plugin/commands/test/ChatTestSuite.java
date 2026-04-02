package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.chat.ChatProfile;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

public final class ChatTestSuite extends TestSuite<ChatController> {
    public ChatTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final ChatController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        playerTest("loadProfile", this::testLoadProfile);
        playerTest("getProfile", this::testGetProfile);
        playerTest("resolveProfile", this::testResolveProfile);
    }

    private void testLoadProfile(final Player player) {
        controller.loadProfile(player).thenAccept(profile -> {
            pass("loadProfile", "loaded profile for " + player.getName());

            assertGetName(profile);
            assertGetPrimaryGroup(profile);
            assertGetWorld(profile);
            assertGetGroups(profile);

            assertDisplayName(profile);
            assertPrefix(profile);
            assertPrefixWithPriority(profile);
            assertGetPrefixes(profile);
            assertSuffix(profile);
            assertSuffixWithPriority(profile);
            assertGetSuffixes(profile);

            assertInfoNodes(profile);
        }).exceptionally(throwable -> {
            fail("loadProfile", throwable.getMessage());
            return null;
        });
    }

    private void testGetProfile(final Player player) {
        final var profile = controller.getProfile(player);
        if (profile.isPresent()) pass("getProfile", "found cached profile");
        else fail("getProfile", "profile not cached after load");
    }

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

    private void assertGetWorld(final ChatProfile profile) {
        final var world = profile.getWorld();
        pass("getWorld", world.map(WorldInfo::getName).orElse("(not set)"));
    }

    private void assertGetGroups(final ChatProfile profile) {
        final var groups = profile.getGroups();
        pass("getGroups", groups.size() + " group(s)");
    }

    private void assertDisplayName(final ChatProfile profile) {
        final var original = profile.getDisplayName().orElse(null);
        pass("getDisplayName", original != null ? original : "(not set)");

        final var set = profile.setDisplayName("service-io-test");
        final var after = profile.getDisplayName().orElse(null);

        if (set) {
            if ("service-io-test".equals(after)) pass("setDisplayName", "value changed");
            else fail("setDisplayName", "returned true but value did not change");
        } else {
            if (original == null ? after == null : original.equals(after))
                pass("setDisplayName", "returned false, value unchanged");
            else fail("setDisplayName", "returned false but value changed");
        }

        profile.setDisplayName(original);
    }

    private void assertPrefix(final ChatProfile profile) {
        final var original = profile.getPrefix().orElse(null);
        pass("getPrefix", original != null ? original : "(not set)");

        final var set = profile.setPrefix("service-io-test");
        final var after = profile.getPrefix().orElse(null);

        if (set) {
            if ("service-io-test".equals(after)) pass("setPrefix", "value changed");
            else fail("setPrefix", "returned true but value did not change");
        } else {
            if (original == null ? after == null : original.equals(after))
                pass("setPrefix", "returned false, value unchanged");
            else fail("setPrefix", "returned false but value changed");
        }

        profile.setPrefix(original);
    }

    private void assertPrefixWithPriority(final ChatProfile profile) {
        final var priority = 10;
        final var original = profile.getPrefix(priority).orElse(null);
        pass("getPrefix(priority)", original != null ? original : "(not set)");

        final var set = profile.setPrefix("service-io-test", priority);
        final var after = profile.getPrefix(priority).orElse(null);

        if (set) {
            if ("service-io-test".equals(after)) pass("setPrefix(priority)", "value changed");
            else fail("setPrefix(priority)", "returned true but value did not change");
        } else {
            if (original == null ? after == null : original.equals(after))
                pass("setPrefix(priority)", "returned false, value unchanged");
            else fail("setPrefix(priority)", "returned false but value changed");
        }

        profile.setPrefix(original, priority);
    }

    private void assertGetPrefixes(final ChatProfile profile) {
        final var prefixes = profile.getPrefixes();
        pass("getPrefixes", prefixes.size() + " prefix(es)");
    }

    private void assertSuffix(final ChatProfile profile) {
        final var original = profile.getSuffix().orElse(null);
        pass("getSuffix", original != null ? original : "(not set)");

        final var set = profile.setSuffix("service-io-test");
        final var after = profile.getSuffix().orElse(null);

        if (set) {
            if ("service-io-test".equals(after)) pass("setSuffix", "value changed");
            else fail("setSuffix", "returned true but value did not change");
        } else {
            if (original == null ? after == null : original.equals(after))
                pass("setSuffix", "returned false, value unchanged");
            else fail("setSuffix", "returned false but value changed");
        }

        profile.setSuffix(original);
    }

    private void assertSuffixWithPriority(final ChatProfile profile) {
        final var priority = 10;
        final var original = profile.getSuffix(priority).orElse(null);
        pass("getSuffix(priority)", original != null ? original : "(not set)");

        final var set = profile.setSuffix("service-io-test", priority);
        final var after = profile.getSuffix(priority).orElse(null);

        if (set) {
            if ("service-io-test".equals(after)) pass("setSuffix(priority)", "value changed");
            else fail("setSuffix(priority)", "returned true but value did not change");
        } else {
            if (original == null ? after == null : original.equals(after))
                pass("setSuffix(priority)", "returned false, value unchanged");
            else fail("setSuffix(priority)", "returned false but value changed");
        }

        profile.setSuffix(original, priority);
    }

    private void assertGetSuffixes(final ChatProfile profile) {
        final var suffixes = profile.getSuffixes();
        pass("getSuffixes", suffixes.size() + " suffix(es)");
    }

    private void assertInfoNodes(final ChatProfile profile) {
        final var key = "service.io.test";

        final var set = profile.setInfoNode(key, "hello");
        if (set) pass("setInfoNode", "set '" + key + "' to 'hello'");
        else fail("setInfoNode", "failed to set info node");

        final var has = profile.hasInfoNode(key);
        if (has) pass("hasInfoNode", "key exists");
        else fail("hasInfoNode", "key not found after set");

        final var value = profile.getInfoNode(key);
        if (value.isPresent() && "hello".equals(value.get())) pass("getInfoNode", "value is 'hello'");
        else fail("getInfoNode", "expected 'hello', got " + value.orElse("(not set)"));

        profile.setInfoNode(key, "true");
        final var boolValue = profile.booleanInfoNode(key);
        if (boolValue.isPresent() && boolValue.get()) pass("booleanInfoNode", "value is true");
        else fail("booleanInfoNode", "expected true, got " + boolValue.map(String::valueOf).orElse("(not set)"));

        profile.setInfoNode(key, "42");
        final var intValue = profile.intInfoNode(key);
        if (intValue.isPresent() && intValue.get() == 42) pass("intInfoNode", "value is 42");
        else fail("intInfoNode", "expected 42, got " + intValue.map(String::valueOf).orElse("(not set)"));

        profile.setInfoNode(key, "3.14");
        final var doubleValue = profile.doubleInfoNode(key);
        if (doubleValue.isPresent() && doubleValue.get() == 3.14) pass("doubleInfoNode", "value is 3.14");
        else fail("doubleInfoNode", "expected 3.14, got " + doubleValue.map(String::valueOf).orElse("(not set)"));

        final var removed = profile.removeInfoNode(key);
        if (removed) pass("removeInfoNode", "removed '" + key + "'");
        else fail("removeInfoNode", "failed to remove info node");

        final var hasAfter = profile.hasInfoNode(key);
        if (!hasAfter) pass("hasInfoNode (after remove)", "key no longer exists");
        else fail("hasInfoNode (after remove)", "key still exists after removal");
    }
}
