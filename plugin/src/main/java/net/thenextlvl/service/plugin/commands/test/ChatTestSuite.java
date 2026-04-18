package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.chat.ChatProfile;
import net.thenextlvl.service.model.MetadataHolder;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public final class ChatTestSuite extends TestSuite<ChatController> {
    public ChatTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final ChatController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        playerAsyncTest("loadProfile", this::testLoadProfile);
        playerTest("getProfile", this::testGetProfile);
        playerAsyncTest("resolveProfile", this::testResolveProfile);
    }

    private CompletableFuture<Void> testLoadProfile(final Player player) {
        return controller.loadProfile(player).thenCompose(profile -> {
            pass("loadProfile", "loaded profile for " + player.getName());

            return lifecycle(
                    () -> assertGetName(profile),
                    () -> assertGetPrimaryGroup(profile),
                    () -> assertGetWorld(profile),
                    () -> assertGetGroups(profile),
                    () -> assertDisplayName(profile),
                    () -> assertPrefix(profile),
                    () -> assertPrefixWithPriority(profile),
                    () -> assertGetPrefixes(profile),
                    () -> assertSuffix(profile),
                    () -> assertSuffixWithPriority(profile),
                    () -> assertGetSuffixes(profile),
                    () -> assertMetadata(profile)
            );
        });
    }

    private void testGetProfile(final Player player) {
        final var profile = controller.getProfile(player);
        assertTrue(profile.isPresent(), "getProfile");
    }

    private CompletableFuture<Void> testResolveProfile(final Player player) {
        return controller.resolveProfile(player).thenAccept(profile ->
                pass("resolveProfile", "resolved profile for " + player.getName()));
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
        pass("getWorld", profile.getWorld().map(world -> world.key().asString()).orElse("(not set)"));
    }

    private void assertGetGroups(final ChatProfile profile) {
        pass("getGroups", profile.getGroups().size() + " group(s)");
    }

    private void assertDisplayName(final ChatProfile profile) {
        final var original = profile.getDisplayName().orElse(null);
        pass("getDisplayName", original != null ? original : "(not set)");

        final var set = profile.setDisplayName("service-io-test");
        final var after = profile.getDisplayName().orElse(null);
        assertChangedValue("setDisplayName", set, original, after, "service-io-test", "value changed");
        profile.setDisplayName(original);
    }

    private void assertPrefix(final ChatProfile profile) {
        final var original = profile.getPrefix().orElse(null);
        pass("getPrefix", original != null ? original : "(not set)");

        final var set = profile.setPrefix("service-io-test");
        final var after = profile.getPrefix().orElse(null);
        assertChangedValue("setPrefix", set, original, after, "service-io-test", "value changed");
        profile.setPrefix(original);
    }

    private void assertPrefixWithPriority(final ChatProfile profile) {
        final var priority = 10;
        final var original = profile.getPrefix(priority).orElse(null);
        pass("getPrefix(priority)", original != null ? original : "(not set)");

        final var set = profile.setPrefix("service-io-test", priority);
        final var after = profile.getPrefix(priority).orElse(null);
        assertChangedValue("setPrefix(priority)", set, original, after, "service-io-test", "value changed");
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
        assertChangedValue("setSuffix", set, original, after, "service-io-test", "value changed");
        profile.setSuffix(original);
    }

    private void assertSuffixWithPriority(final ChatProfile profile) {
        final var priority = 10;
        final var original = profile.getSuffix(priority).orElse(null);
        pass("getSuffix(priority)", original != null ? original : "(not set)");

        final var set = profile.setSuffix("service-io-test", priority);
        final var after = profile.getSuffix(priority).orElse(null);
        assertChangedValue("setSuffix(priority)", set, original, after, "service-io-test", "value changed");
        profile.setSuffix(original, priority);
    }

    private void assertGetSuffixes(final ChatProfile profile) {
        final var suffixes = profile.getSuffixes();
        pass("getSuffixes", suffixes.size() + " suffix(es)");
    }

    private void assertMetadata(final ChatProfile profile) {
        if (!(profile instanceof final MetadataHolder metadata)) {
            skip("metadata", "metadata unsupported");
            return;
        }

        assertInfoNodes(metadata);
    }

    private void assertInfoNodes(final MetadataHolder profile) {
        final var key = "service.io.test";

        final var set = profile.setInfoNode(key, "hello");
        final var has = profile.hasInfoNode(key);
        final var value = profile.getInfoNode(key);
        assertRequiredStateChange("setInfoNode", set, has && value.isPresent() && "hello".equals(value.get()),
                "set '" + key + "' to 'hello'",
                "failed to set info node",
                !has ? "key not found after set" : "expected 'hello', got " + value.orElse("(not set)"));
        assertState("hasInfoNode", has, "key exists", "key not found after set");
        assertState("getInfoNode", value.isPresent() && "hello".equals(value.get()),
                "value is 'hello'",
                "expected 'hello', got " + value.orElse("(not set)"));

        final var setBoolean = profile.setInfoNode(key, "true");
        final var boolValue = profile.booleanInfoNode(key);
        assertRequiredStateChange("setInfoNode(boolean)", setBoolean, boolValue.isPresent() && boolValue.get(),
                "set '" + key + "' to 'true'",
                "failed to set info node to 'true'",
                "booleanInfoNode did not return true");
        assertState("booleanInfoNode", boolValue.isPresent() && boolValue.get(),
                "value is true",
                "expected true, got " + boolValue.map(String::valueOf).orElse("(not set)"));

        final var setInt = profile.setInfoNode(key, "42");
        final var intValue = profile.intInfoNode(key);
        assertRequiredStateChange("setInfoNode(int)", setInt, intValue.isPresent() && intValue.get() == 42,
                "set '" + key + "' to '42'",
                "failed to set info node to '42'",
                "intInfoNode did not return 42");
        assertState("intInfoNode", intValue.isPresent() && intValue.get() == 42,
                "value is 42",
                "expected 42, got " + intValue.map(String::valueOf).orElse("(not set)"));

        final var setDouble = profile.setInfoNode(key, "3.14");
        final var doubleValue = profile.doubleInfoNode(key);
        assertRequiredStateChange("setInfoNode(double)", setDouble,
                doubleValue.isPresent() && doubleValue.get() == 3.14,
                "set '" + key + "' to '3.14'",
                "failed to set info node to '3.14'",
                "doubleInfoNode did not return 3.14");
        assertState("doubleInfoNode", doubleValue.isPresent() && doubleValue.get() == 3.14,
                "value is 3.14",
                "expected 3.14, got " + doubleValue.map(String::valueOf).orElse("(not set)"));

        final var removed = profile.removeInfoNode(key);
        final var hasAfter = profile.hasInfoNode(key);
        assertRequiredStateChange("removeInfoNode", removed, !hasAfter,
                "removed '" + key + "'",
                "failed to remove info node",
                "key still exists after removal");
        assertState("hasInfoNode (after remove)", !hasAfter,
                "key no longer exists",
                "key still exists after removal");
    }
}
