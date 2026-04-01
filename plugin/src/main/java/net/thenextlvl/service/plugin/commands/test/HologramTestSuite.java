package net.thenextlvl.service.plugin.commands.test;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.hologram.Hologram;
import net.thenextlvl.service.hologram.HologramCapability;
import net.thenextlvl.service.hologram.HologramController;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class HologramTestSuite implements TestSuite<HologramController> {
    @Override
    public Class<HologramController> controllerType() {
        return HologramController.class;
    }

    @Override
    public void run(final CommandSender sender, final HologramController controller) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component.text("This test must be run by a player", NamedTextColor.RED));
            return;
        }

        testGetCapabilities(sender, controller);
        testGetHolograms(sender, controller);

        final var name = "service-io-test";
        final var hologram = testCreateHologram(sender, controller, name, player);
        if (hologram == null) return;

        testGetHologram(sender, controller, name);
        testGetHologramsByWorld(sender, controller, player);
        testGetHologramsByPlayer(sender, controller, player);

        testGetName(sender, hologram);
        testSetName(sender, hologram);
        testGetLocation(sender, hologram);
        testTeleport(sender, hologram, player);

        testIsPersistent(sender, hologram);
        testSetPersistent(sender, hologram);

        testVisibility(sender, hologram, player);

        if (controller.hasCapability(HologramCapability.TEXT_LINES)) {
            testTextLine(sender, hologram, player);
        } else {
            skip(sender, "addTextLine", "TEXT_LINES capability not available");
        }

        testLineManagement(sender, hologram, controller);

        testDeleteHologram(sender, controller, hologram);
        testGetHologramEmpty(sender, controller, name);
    }

    private void testGetCapabilities(final CommandSender sender, final HologramController controller) {
        final var capabilities = controller.getCapabilities();
        pass(sender, "getCapabilities", capabilities.toString());
    }

    private void testGetHolograms(final CommandSender sender, final HologramController controller) {
        final var holograms = controller.getHolograms();
        pass(sender, "getHolograms", holograms.size() + " hologram(s)");
    }

    private @Nullable Hologram testCreateHologram(final CommandSender sender, final HologramController controller, final String name, final Player player) {
        try {
            final var hologram = controller.createHologram(name, player.getLocation());
            pass(sender, "createHologram", "created '" + hologram.getName() + "'");
            return hologram;
        } catch (final Exception e) {
            fail(sender, "createHologram", e.getMessage());
            return null;
        }
    }

    private void testGetHologram(final CommandSender sender, final HologramController controller, final String name) {
        final var hologram = controller.getHologram(name);
        if (hologram.isPresent()) pass(sender, "getHologram", "found '" + name + "'");
        else fail(sender, "getHologram", "hologram '" + name + "' not found after creation");
    }

    private void testGetHologramsByWorld(final CommandSender sender, final HologramController controller, final Player player) {
        final var holograms = controller.getHolograms(player.getWorld());
        pass(sender, "getHolograms(world)", holograms.size() + " hologram(s) in " + player.getWorld().getName());
    }

    private void testGetHologramsByPlayer(final CommandSender sender, final HologramController controller, final Player player) {
        final var holograms = controller.getHolograms(player);
        pass(sender, "getHolograms(player)", holograms.size() + " hologram(s) for " + player.getName());
    }

    private void testGetName(final CommandSender sender, final Hologram hologram) {
        pass(sender, "getName", hologram.getName());
    }

    private void testSetName(final CommandSender sender, final Hologram hologram) {
        final var original = hologram.getName();
        final var renamed = hologram.setName("service-io-test-renamed");
        if (renamed) {
            pass(sender, "setName", "renamed to '" + hologram.getName() + "'");
            hologram.setName(original);
        } else {
            fail(sender, "setName", "failed to rename hologram");
        }
    }

    private void testGetLocation(final CommandSender sender, final Hologram hologram) {
        final var loc = hologram.getLocation();
        pass(sender, "getLocation", String.format("%.1f, %.1f, %.1f in %s", loc.getX(), loc.getY(), loc.getZ(), hologram.getWorld().getName()));
    }

    private void testTeleport(final CommandSender sender, final Hologram hologram, final Player player) {
        final var target = player.getLocation().add(0, 2, 0);
        hologram.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass(sender, "teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail(sender, "teleportAsync", "teleport returned false");
        }).exceptionally(throwable -> {
            fail(sender, "teleportAsync", throwable.getMessage());
            return null;
        });
    }

    private void testIsPersistent(final CommandSender sender, final Hologram hologram) {
        pass(sender, "isPersistent", String.valueOf(hologram.isPersistent()));
    }

    private void testSetPersistent(final CommandSender sender, final Hologram hologram) {
        final var original = hologram.isPersistent();
        final var changed = hologram.setPersistent(!original);
        if (changed) {
            pass(sender, "setPersistent", "changed to " + hologram.isPersistent());
            hologram.setPersistent(original);
        } else {
            pass(sender, "setPersistent", "already " + original + " (no change)");
        }
    }

    private void testVisibility(final CommandSender sender, final Hologram hologram, final Player player) {
        pass(sender, "isVisibleByDefault", String.valueOf(hologram.isVisibleByDefault()));
        pass(sender, "canSee", String.valueOf(hologram.canSee(player)));
        pass(sender, "isTrackedBy", String.valueOf(hologram.isTrackedBy(player)));

        final var added = hologram.addViewer(player.getUniqueId());
        pass(sender, "addViewer", added ? "added" : "already a viewer");

        if (hologram.isViewer(player.getUniqueId())) pass(sender, "isViewer", "true");
        else fail(sender, "isViewer", "false after addViewer");

        pass(sender, "getViewers", hologram.getViewers().size() + " viewer(s)");

        final var removed = hologram.removeViewer(player.getUniqueId());
        pass(sender, "removeViewer", removed ? "removed" : "was not a viewer");
    }

    private void testTextLine(final CommandSender sender, final Hologram hologram, final Player player) {
        try {
            final var line = hologram.addTextLine();
            pass(sender, "addTextLine", "added text line at index " + hologram.getLineIndex(line));

            final var text = Component.text("Hello from ServiceIO test!", NamedTextColor.GOLD);
            final var changed = line.setText(text);
            if (changed) pass(sender, "setText", "set text content");
            else fail(sender, "setText", "text was not changed");

            final var retrieved = line.getText(player);
            if (retrieved.isPresent()) pass(sender, "getText", "text content present");
            else fail(sender, "getText", "text content empty after setText");

            pass(sender, "getLineWidth", String.valueOf(line.getLineWidth()));
            pass(sender, "getAlignment", String.valueOf(line.getAlignment()));
            pass(sender, "isShadowed", String.valueOf(line.isShadowed()));
            pass(sender, "isSeeThrough", String.valueOf(line.isSeeThrough()));

            pass(sender, "getLineCount", String.valueOf(hologram.getLineCount()));
        } catch (final Exception e) {
            fail(sender, "textLine tests", e.getMessage());
        }
    }

    private void testLineManagement(final CommandSender sender, final Hologram hologram, final HologramController controller) {
        if (!controller.hasCapability(HologramCapability.TEXT_LINES)) {
            skip(sender, "line management", "TEXT_LINES capability not available");
            return;
        }

        try {
            hologram.addTextLine();
            hologram.addTextLine();
            pass(sender, "getLineCount (after adding)", String.valueOf(hologram.getLineCount()));

            if (hologram.getLineCount() >= 2) {
                final var swapped = hologram.swapLines(0, 1);
                pass(sender, "swapLines(0, 1)", swapped ? "swapped" : "not swapped");

                final var moved = hologram.moveLine(1, 0);
                pass(sender, "moveLine(1, 0)", moved ? "moved" : "not moved");
            }

            final var line = hologram.getLine(0);
            if (line.isPresent()) {
                pass(sender, "getLine(0)", "type: " + line.get().getType());
                pass(sender, "hasLine", String.valueOf(hologram.hasLine(line.get())));
                final var removed = hologram.removeLine(line.get());
                pass(sender, "removeLine", removed ? "removed line 0" : "failed to remove");
            } else {
                fail(sender, "getLine(0)", "no line at index 0");
            }

            final var cleared = hologram.clearLines();
            pass(sender, "clearLines", cleared ? "cleared all lines" : "no lines to clear");
        } catch (final Exception e) {
            fail(sender, "line management", e.getMessage());
        }
    }

    private void testDeleteHologram(final CommandSender sender, final HologramController controller, final Hologram hologram) {
        final var deleted = controller.deleteHologram(hologram);
        if (deleted) pass(sender, "deleteHologram", "deleted '" + hologram.getName() + "'");
        else fail(sender, "deleteHologram", "failed to delete hologram");
    }

    private void testGetHologramEmpty(final CommandSender sender, final HologramController controller, final String name) {
        final var hologram = controller.getHologram(name);
        if (hologram.isEmpty()) pass(sender, "getHologram (after delete)", "hologram no longer found");
        else fail(sender, "getHologram (after delete)", "hologram still found after deletion");
    }
}
