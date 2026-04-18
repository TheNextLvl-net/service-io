package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.hologram.line.TextHologramLine;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class HologramTestSuite extends TestSuite<HologramController> {
    public HologramTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final HologramController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getCapabilities", this::testGetCapabilities);
        test("getHolograms", this::testGetHolograms);
        asyncTest("hologramLifecycle", this::testHologramLifecycle);
        playerTest("playerHolograms", this::testPlayerHolograms);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.toString());
    }

    private void testGetHolograms() {
        final var holograms = controller.getHolograms();
        pass("getHolograms", holograms.size() + " hologram(s)");
    }

    private CompletableFuture<Void> testHologramLifecycle() {
        final var name = "service-io-test";
        final var hologram = createHologram(name);
        if (hologram == null) return CompletableFuture.completedFuture(null);

        return lifecycle(
                () -> assertHologramFound(name),
                () -> assertHologramsByWorld(hologram),
                () -> assertGetName(hologram),
                () -> assertSetName(hologram),
                () -> assertGetLocation(hologram),
                () -> assertGetWorld(hologram)
        ).thenCompose(ignored -> lifecycleAsync(
                () -> assertTeleport(hologram)
        )).thenCompose(ignored -> lifecycle(
                () -> assertIsPersistent(hologram),
                () -> assertSetPersistent(hologram),
                () -> assertPersist(hologram),
                () -> assertViewPermission(hologram),
                () -> assertVisibleByDefault(hologram),
                () -> assertGetTrackedBy(hologram),
                () -> assertGetLines(hologram),
                () -> assertLineManagement(hologram),
                () -> assertDeleteHologram(hologram),
                () -> assertHologramNotFound(name)
        ));
    }

    private void testPlayerHolograms(final Player player) {
        final var name = "service-io-test-player";
        final var hologram = createHologram(name);
        if (hologram == null) return;

        lifecycle(
                () -> assertHologramsByPlayer(player),
                () -> assertVisibility(hologram, player),
                () -> {
                    if (controller.hasCapability(HologramCapability.TEXT_LINES)) {
                        assertTextLine(hologram, player);
                    } else {
                        skip("addTextLine", "TEXT_LINES capability not available");
                    }
                },
                () -> assertDeleteHologram(hologram)
        );
    }

    private @Nullable Hologram createHologram(final String name) {
        try {
            final var hologram = controller.createHologram(name, source.getLocation());
            pass("createHologram", "created '" + hologram.getName() + "'");
            return hologram;
        } catch (final Exception e) {
            fail("createHologram", e.getMessage());
            return null;
        }
    }

    private void assertHologramFound(final String name) {
        final var hologram = controller.getHologram(name);
        assertTrue(hologram.isPresent(), "getHologram");
    }

    private void assertHologramNotFound(final String name) {
        final var hologram = controller.getHologram(name);
        assertTrue(hologram.isEmpty(), "getHologram (after delete)");
    }

    private void assertHologramsByWorld(final Hologram hologram) {
        final var world = hologram.getWorld();
        final var holograms = controller.getHolograms(world);
        pass("getHolograms(world)", holograms.size() + " hologram(s) in " + world.key().asString());
    }

    private void assertHologramsByPlayer(final Player player) {
        final var holograms = controller.getHolograms(player);
        pass("getHolograms(player)", holograms.size() + " hologram(s) for " + player.getName());
    }

    private void assertGetName(final Hologram hologram) {
        pass("getName", hologram.getName());
    }

    private void assertSetName(final Hologram hologram) {
        final var original = hologram.getName();
        final var renamed = hologram.setName("service-io-test-renamed");
        final var updated = hologram.getName();
        assertChangedValue("setName", renamed, original, updated, "service-io-test-renamed", "renamed to '" + updated + "'");
        hologram.setName(original);
    }

    private void assertGetLocation(final Hologram hologram) {
        final var loc = hologram.getLocation();
        pass("getLocation", String.format("%.1f, %.1f, %.1f in %s", loc.getX(), loc.getY(), loc.getZ(), hologram.getWorld().key().asString()));
    }

    private void assertGetWorld(final Hologram hologram) {
        final var world = hologram.getWorld();
        pass("getWorld", world.key().asString());
    }

    private CompletableFuture<Void> assertTeleport(final Hologram hologram) {
        final var target = source.getLocation().add(0, 2, 0);
        return hologram.teleportAsync(target).thenAccept(success -> {
            final var location = hologram.getLocation();
            final var matches = location.getWorld() != null
                    && location.getWorld().equals(target.getWorld())
                    && location.distanceSquared(target) == 0;
            assertRequiredStateChange("teleportAsync", success, matches,
                    String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()),
                    "teleport returned false",
                    String.format("teleportAsync returned true but location is %.1f, %.1f, %.1f",
                            location.getX(), location.getY(), location.getZ()));
        });
    }

    private void assertIsPersistent(final Hologram hologram) {
        pass("isPersistent", String.valueOf(hologram.isPersistent()));
    }

    private void assertSetPersistent(final Hologram hologram) {
        final var original = hologram.isPersistent();
        final var changed = hologram.setPersistent(!original);
        final var updated = hologram.isPersistent();
        assertChangedValue("setPersistent", changed, original, updated, !original, "changed to " + updated);
        hologram.setPersistent(original);
    }

    private void assertPersist(final Hologram hologram) {
        final var persisted = hologram.persist();
        pass("persist", persisted ? "persisted" : "not persisted");
    }

    private void assertViewPermission(final Hologram hologram) {
        final var originalPermission = hologram.getViewPermission();
        pass("getViewPermission", originalPermission.map(p -> "'" + p + "'").orElse("empty"));

        final var changed = hologram.setViewPermission("service.test.view");
        final var retrieved = hologram.getViewPermission().orElse(null);
        assertChangedValue("setViewPermission", changed, originalPermission.orElse(null), retrieved,
                "service.test.view", "set to 'service.test.view'");

        hologram.setViewPermission(originalPermission.orElse(null));
    }

    private void assertVisibleByDefault(final Hologram hologram) {
        final var original = hologram.isVisibleByDefault();
        pass("isVisibleByDefault", String.valueOf(original));

        final var changed = hologram.setVisibleByDefault(!original);
        final var updated = hologram.isVisibleByDefault();
        assertChangedValue("setVisibleByDefault", changed, original, updated, !original, "changed to " + updated);
        hologram.setVisibleByDefault(original);
    }

    private void assertGetTrackedBy(final Hologram hologram) {
        final var tracked = hologram.getTrackedBy().toList();
        pass("getTrackedBy", tracked.size() + " player(s)");
    }

    private void assertGetLines(final Hologram hologram) {
        final var lines = hologram.getLines().toList();
        pass("getLines", lines.size() + " line(s)");
        pass("getLineCount", String.valueOf(hologram.getLineCount()));
    }

    private void assertVisibility(final Hologram hologram, final Player player) {
        pass("isVisibleByDefault", String.valueOf(hologram.isVisibleByDefault()));
        pass("canSee", String.valueOf(hologram.canSee(player)));
        pass("isTrackedBy", String.valueOf(hologram.isTrackedBy(player)));

        final var viewerBeforeAdd = hologram.isViewer(player.getUniqueId());
        final var added = hologram.addViewer(player.getUniqueId());
        final var isViewerAfterAdd = hologram.isViewer(player.getUniqueId());
        assertChangedValue("addViewer", added, viewerBeforeAdd, isViewerAfterAdd, true, "added");

        pass("isViewer", String.valueOf(hologram.isViewer(player.getUniqueId())));
        pass("getViewers", hologram.getViewers().size() + " viewer(s)");

        final var viewerBeforeBatchAdd = hologram.isViewer(player.getUniqueId());
        final var addedBatch = hologram.addViewers(List.of(player.getUniqueId()));
        final var isViewerAfterBatchAdd = hologram.isViewer(player.getUniqueId());
        assertChangedValue("addViewers(Collection)", addedBatch, viewerBeforeBatchAdd, isViewerAfterBatchAdd, true, "added");

        final var viewerBeforeRemove = hologram.isViewer(player.getUniqueId());
        final var removed = hologram.removeViewer(player.getUniqueId());
        final var isViewerAfterRemove = hologram.isViewer(player.getUniqueId());
        assertChangedValue("removeViewer", removed, viewerBeforeRemove, isViewerAfterRemove, false, "removed");

        hologram.addViewer(player.getUniqueId());
        final var viewerBeforeBatchRemove = hologram.isViewer(player.getUniqueId());
        final var removedBatch = hologram.removeViewers(List.of(player.getUniqueId()));
        final var isViewerAfterBatchRemove = hologram.isViewer(player.getUniqueId());
        assertChangedValue("removeViewers(Collection)", removedBatch, viewerBeforeBatchRemove, isViewerAfterBatchRemove, false, "removed");
    }

    private void assertTextLine(final Hologram hologram, final Player player) {
        if (!controller.hasCapability(HologramCapability.TEXT_LINES)) {
            skip("textLine tests", "TEXT_LINES capability not available");
            return;
        }

        try {
            final var beforeCount = hologram.getLineCount();
            final var line = hologram.addTextLine();
            final var afterCount = hologram.getLineCount();
            assertRequiredStateChange("addTextLine", true, afterCount == beforeCount + 1 && hologram.hasLine(line),
                    "added text line at index " + hologram.getLineIndex(line),
                    "failed to add text line",
                    "line count did not increase or hologram does not track the new line");

            assertTextLineGetHologram(line, hologram);
            assertTextLineGetType(line);
            assertTextLineGetWorld(line);

            assertTextLineSetText(line, player);
            assertTextLineUnparsedText(line);

            assertTextLineLineWidth(line);
            assertTextLineBackgroundColor(line);
            assertTextLineTextOpacity(line);
            assertTextLineShadowed(line);
            assertTextLineSeeThrough(line);
            assertTextLineDefaultBackground(line);
            assertTextLineAlignment(line);
            assertTextLineViewPermission(line);
            assertTextLineCanSee(line, player);

            pass("getLineCount", String.valueOf(hologram.getLineCount()));

            final var lineByIndex = hologram.getLine(0);
            assertTrue(lineByIndex.isPresent(), "getLine(int)");

            final var typedLine = hologram.getLine(0, TextHologramLine.class);
            assertTrue(typedLine.isPresent(), "getLine(int, Class)");
        } catch (final Exception e) {
            fail("textLine tests", e.getMessage());
        }
    }

    private void assertTextLineGetHologram(final TextHologramLine line, final Hologram hologram) {
        final var parent = line.getHologram();
        if (parent == hologram) pass("TextHologramLine.getHologram", "matches parent hologram");
        else fail("TextHologramLine.getHologram", "does not match parent hologram");
    }

    private void assertTextLineGetType(final TextHologramLine line) {
        pass("TextHologramLine.getType", String.valueOf(line.getType()));
    }

    private void assertTextLineGetWorld(final TextHologramLine line) {
        pass("TextHologramLine.getWorld", line.getWorld().key().asString());
    }

    private void assertTextLineSetText(final TextHologramLine line, final Player player) {
        final var text = Component.text("Hello from ServiceIO test!", NamedTextColor.GOLD);
        final var changed = line.setText(text);
        final var retrieved = line.getText(player);
        assertRequiredStateChange("setText / getText", changed, retrieved.isPresent(),
                "text content verified",
                "text was not changed",
                "setText returned true but getText is empty");
    }

    private void assertTextLineUnparsedText(final TextHologramLine line) {
        final var original = line.getUnparsedText();
        pass("getUnparsedText", original.map(t -> "'" + t + "'").orElse("empty"));

        final var changed = line.setUnparsedText("<gold>Unparsed test</gold>");
        final var retrieved = line.getUnparsedText().orElse(null);
        assertChangedValue("setUnparsedText", changed, original.orElse(null), retrieved,
                "<gold>Unparsed test</gold>", "set to '" + retrieved + "'");

        line.setUnparsedText(original.orElse(null));
    }

    private void assertTextLineLineWidth(final TextHologramLine line) {
        final var original = line.getLineWidth();
        pass("getLineWidth", String.valueOf(original));

        final var newWidth = original == 200 ? 150 : 200;
        final var changed = line.setLineWidth(newWidth);
        final var updated = line.getLineWidth();
        assertChangedValue("setLineWidth", changed, original, updated, newWidth, "changed to " + newWidth);
        line.setLineWidth(original);
    }

    private void assertTextLineBackgroundColor(final TextHologramLine line) {
        final var original = line.getBackgroundColor();
        pass("getBackgroundColor", original.map(Color::toString).orElse("empty"));

        final var changed = line.setBackgroundColor(Color.RED);
        final var retrieved = line.getBackgroundColor().orElse(null);
        assertChangedValue("setBackgroundColor", changed, original.orElse(null), retrieved, Color.RED, "set to " + retrieved);

        line.setBackgroundColor(original.orElse(null));
    }

    private void assertTextLineTextOpacity(final TextHologramLine line) {
        final var original = line.getTextOpacity();
        pass("getTextOpacity", String.valueOf(original));

        final var newOpacity = original == 100 ? 50 : 100;
        final var changed = line.setTextOpacity(newOpacity);
        final var updated = line.getTextOpacity();
        assertChangedValue("setTextOpacity", changed, original, updated, newOpacity, "changed to " + newOpacity);
        line.setTextOpacity(original);
    }

    private void assertTextLineShadowed(final TextHologramLine line) {
        final var original = line.isShadowed();
        pass("isShadowed", String.valueOf(original));

        final var changed = line.setShadowed(!original);
        final var updated = line.isShadowed();
        assertChangedValue("setShadowed", changed, original, updated, !original, "changed to " + updated);
        line.setShadowed(original);
    }

    private void assertTextLineSeeThrough(final TextHologramLine line) {
        final var original = line.isSeeThrough();
        pass("isSeeThrough", String.valueOf(original));

        final var changed = line.setSeeThrough(!original);
        final var updated = line.isSeeThrough();
        assertChangedValue("setSeeThrough", changed, original, updated, !original, "changed to " + updated);
        line.setSeeThrough(original);
    }

    private void assertTextLineDefaultBackground(final TextHologramLine line) {
        final var original = line.isDefaultBackground();
        pass("isDefaultBackground", String.valueOf(original));

        final var changed = line.setDefaultBackground(!original);
        final var updated = line.isDefaultBackground();
        assertChangedValue("setDefaultBackground", changed, original, updated, !original, "changed to " + updated);
        line.setDefaultBackground(original);
    }

    private void assertTextLineAlignment(final TextHologramLine line) {
        final var original = line.getAlignment();
        pass("getAlignment", String.valueOf(original));

        final var newAlignment = original == TextAlignment.CENTER ? TextAlignment.LEFT : TextAlignment.CENTER;
        final var changed = line.setAlignment(newAlignment);
        final var updated = line.getAlignment();
        assertChangedValue("setAlignment", changed, original, updated, newAlignment, "changed to " + newAlignment);
        line.setAlignment(original);
    }

    private void assertTextLineViewPermission(final TextHologramLine line) {
        final var original = line.getViewPermission();
        pass("HologramLine.getViewPermission", original.map(p -> "'" + p + "'").orElse("empty"));

        final var changed = line.setViewPermission("service.test.line.view");
        final var retrieved = line.getViewPermission().orElse(null);
        assertChangedValue("HologramLine.setViewPermission", changed, original.orElse(null), retrieved,
                "service.test.line.view", "set to 'service.test.line.view'");

        line.setViewPermission(original.orElse(null));
    }

    private void assertTextLineCanSee(final TextHologramLine line, final Player player) {
        pass("HologramLine.canSee", String.valueOf(line.canSee(player)));
    }

    private void assertLineManagement(final Hologram hologram) {
        if (!controller.hasCapability(HologramCapability.TEXT_LINES)) {
            skip("line management", "TEXT_LINES capability not available");
            return;
        }

        try {
            hologram.addTextLine();
            hologram.addTextLine();
            assertState("getLineCount (after adding)", hologram.getLineCount() >= 2,
                    String.valueOf(hologram.getLineCount()),
                    "expected at least 2 lines after adding but got " + hologram.getLineCount());

            final var lines = hologram.getLines().toList();
            assertState("getLines (after adding)", lines.size() >= 2,
                    lines.size() + " line(s)",
                    "expected at least 2 lines after adding but got " + lines.size());

            if (hologram.getLineCount() >= 2) {
                final var swapped = hologram.swapLines(0, 1);
                assertState("swapLines(0, 1)", swapped, "swapped", "not swapped");

                final var moved = hologram.moveLine(1, 0);
                assertState("moveLine(1, 0)", moved, "moved", "not moved");
            }

            final var line = hologram.getLine(0);
            if (line.isPresent()) {
                pass("getLine(0)", "type: " + line.get().getType());

                final var typedLine = hologram.getLine(0, TextHologramLine.class);
                if (typedLine.isPresent()) pass("getLine(0, TextHologramLine.class)", "found");
                else fail("getLine(0, TextHologramLine.class)", "not found");

                assertState("hasLine", hologram.hasLine(line.get()), "true", "expected line to be tracked");
                final var removed = hologram.removeLine(line.get());
                assertRequiredStateChange("removeLine", removed, !hologram.hasLine(line.get()),
                        "removed line 0",
                        "failed to remove",
                        "removeLine returned true but hologram still has the line");
            } else {
                fail("getLine(0)", "no line at index 0");
            }

            final var beforeClearCount = hologram.getLineCount();
            final var cleared = hologram.clearLines();
            final var remaining = hologram.getLineCount();
            assertChangedValue("clearLines", cleared, beforeClearCount, remaining, 0, "cleared all lines");
        } catch (final Exception e) {
            fail("line management", e.getMessage());
        }
    }

    private void assertDeleteHologram(final Hologram hologram) {
        final var deleted = controller.deleteHologram(hologram);
        assertTrue(deleted, "deleteHologram");
    }
}
