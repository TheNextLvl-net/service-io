package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class HologramTestSuite extends TestSuite<HologramController> {
    public HologramTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final HologramController controller) {
        super(plugin, source, controller);
    }

    @Override
    public void run() {
        final var player = source.getSender() instanceof final Player p ? p : null;

        testGetCapabilities();
        testGetHolograms();

        final var name = "service-io-test";
        final var hologram = testCreateHologram(name);
        if (hologram == null) return;

        testGetHologram(name);
        testGetHologramsByWorld(source.getLocation().getWorld());
        if (player != null) testGetHologramsByPlayer(player);

        testGetName(hologram);
        testSetName(hologram);
        testGetLocation(hologram);
        testTeleport(hologram);

        testIsPersistent(hologram);
        testSetPersistent(hologram);

        if (player != null) {
            testVisibility(hologram, player);

            if (controller.hasCapability(HologramCapability.TEXT_LINES)) {
                testTextLine(hologram, player);
            } else {
                skip("addTextLine", "TEXT_LINES capability not available");
            }
        }

        testLineManagement(hologram);

        testDeleteHologram(hologram);
        testGetHologramEmpty(name);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.toString());
    }

    private void testGetHolograms() {
        final var holograms = controller.getHolograms();
        pass("getHolograms", holograms.size() + " hologram(s)");
    }

    private @Nullable Hologram testCreateHologram(final String name) {
        try {
            final var hologram = controller.createHologram(name, source.getLocation());
            pass("createHologram", "created '" + hologram.getName() + "'");
            return hologram;
        } catch (final Exception e) {
            fail("createHologram", e.getMessage());
            return null;
        }
    }

    private void testGetHologram(final String name) {
        final var hologram = controller.getHologram(name);
        if (hologram.isPresent()) pass("getHologram", "found '" + name + "'");
        else fail("getHologram", "hologram '" + name + "' not found after creation");
    }

    private void testGetHologramsByWorld(final World world) {
        final var holograms = controller.getHolograms(world);
        pass("getHolograms(world)", holograms.size() + " hologram(s) in " + world.getName());
    }

    private void testGetHologramsByPlayer(final Player player) {
        final var holograms = controller.getHolograms(player);
        pass("getHolograms(player)", holograms.size() + " hologram(s) for " + player.getName());
    }

    private void testGetName(final Hologram hologram) {
        pass("getName", hologram.getName());
    }

    private void testSetName(final Hologram hologram) {
        final var original = hologram.getName();
        final var renamed = hologram.setName("service-io-test-renamed");
        if (renamed) {
            pass("setName", "renamed to '" + hologram.getName() + "'");
            hologram.setName(original);
        } else {
            fail("setName", "failed to rename hologram");
        }
    }

    private void testGetLocation(final Hologram hologram) {
        final var loc = hologram.getLocation();
        pass("getLocation", String.format("%.1f, %.1f, %.1f in %s", loc.getX(), loc.getY(), loc.getZ(), hologram.getWorld().getName()));
    }

    private void testTeleport(final Hologram hologram) {
        final var target = source.getLocation().add(0, 2, 0);
        hologram.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass("teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail("teleportAsync", "teleport returned false");
        }).exceptionally(throwable -> {
            fail("teleportAsync", throwable.getMessage());
            return null;
        });
    }

    private void testIsPersistent(final Hologram hologram) {
        pass("isPersistent", String.valueOf(hologram.isPersistent()));
    }

    private void testSetPersistent(final Hologram hologram) {
        final var original = hologram.isPersistent();
        final var changed = hologram.setPersistent(!original);
        if (changed) {
            pass("setPersistent", "changed to " + hologram.isPersistent());
            hologram.setPersistent(original);
        } else {
            pass("setPersistent", "already " + original + " (no change)");
        }
    }

    private void testVisibility(final Hologram hologram, final Player player) {
        pass("isVisibleByDefault", String.valueOf(hologram.isVisibleByDefault()));
        pass("canSee", String.valueOf(hologram.canSee(player)));
        pass("isTrackedBy", String.valueOf(hologram.isTrackedBy(player)));

        final var added = hologram.addViewer(player.getUniqueId());
        pass("addViewer", added ? "added" : "already a viewer");

        if (hologram.isViewer(player.getUniqueId())) pass("isViewer", "true");
        else fail("isViewer", "false after addViewer");

        pass("getViewers", hologram.getViewers().size() + " viewer(s)");

        final var removed = hologram.removeViewer(player.getUniqueId());
        pass("removeViewer", removed ? "removed" : "was not a viewer");
    }

    private void testTextLine(final Hologram hologram, final Player player) {
        try {
            final var line = hologram.addTextLine();
            pass("addTextLine", "added text line at index " + hologram.getLineIndex(line));

            final var text = Component.text("Hello from ServiceIO test!", NamedTextColor.GOLD);
            final var changed = line.setText(text);
            if (changed) pass("setText", "set text content");
            else fail("setText", "text was not changed");

            final var retrieved = line.getText(player);
            if (retrieved.isPresent()) pass("getText", "text content present");
            else fail("getText", "text content empty after setText");

            pass("getLineWidth", String.valueOf(line.getLineWidth()));
            pass("getAlignment", String.valueOf(line.getAlignment()));
            pass("isShadowed", String.valueOf(line.isShadowed()));
            pass("isSeeThrough", String.valueOf(line.isSeeThrough()));

            pass("getLineCount", String.valueOf(hologram.getLineCount()));
        } catch (final Exception e) {
            fail("textLine tests", e.getMessage());
        }
    }

    private void testLineManagement(final Hologram hologram) {
        if (!controller.hasCapability(HologramCapability.TEXT_LINES)) {
            skip("line management", "TEXT_LINES capability not available");
            return;
        }

        try {
            hologram.addTextLine();
            hologram.addTextLine();
            pass("getLineCount (after adding)", String.valueOf(hologram.getLineCount()));

            if (hologram.getLineCount() >= 2) {
                final var swapped = hologram.swapLines(0, 1);
                pass("swapLines(0, 1)", swapped ? "swapped" : "not swapped");

                final var moved = hologram.moveLine(1, 0);
                pass("moveLine(1, 0)", moved ? "moved" : "not moved");
            }

            final var line = hologram.getLine(0);
            if (line.isPresent()) {
                pass("getLine(0)", "type: " + line.get().getType());
                pass("hasLine", String.valueOf(hologram.hasLine(line.get())));
                final var removed = hologram.removeLine(line.get());
                pass("removeLine", removed ? "removed line 0" : "failed to remove");
            } else {
                fail("getLine(0)", "no line at index 0");
            }

            final var cleared = hologram.clearLines();
            pass("clearLines", cleared ? "cleared all lines" : "no lines to clear");
        } catch (final Exception e) {
            fail("line management", e.getMessage());
        }
    }

    private void testDeleteHologram(final Hologram hologram) {
        final var deleted = controller.deleteHologram(hologram);
        if (deleted) pass("deleteHologram", "deleted '" + hologram.getName() + "'");
        else fail("deleteHologram", "failed to delete hologram");
    }

    private void testGetHologramEmpty(final String name) {
        final var hologram = controller.getHologram(name);
        if (hologram.isEmpty()) pass("getHologram (after delete)", "hologram no longer found");
        else fail("getHologram (after delete)", "hologram still found after deletion");
    }
}
