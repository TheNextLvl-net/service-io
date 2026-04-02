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

public final class HologramTestSuite extends TestSuite<HologramController> {
    public HologramTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final HologramController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getCapabilities", this::testGetCapabilities);
        test("getHolograms", this::testGetHolograms);
        test("hologramLifecycle", this::testHologramLifecycle);
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

    private void testHologramLifecycle() {
        final var name = "service-io-test";
        final var hologram = createHologram(name);
        if (hologram == null) return;

        assertHologramFound(name);
        assertHologramsByWorld(hologram);

        assertGetName(hologram);
        assertSetName(hologram);
        assertGetLocation(hologram);
        assertGetWorld(hologram);
        assertTeleport(hologram);

        assertIsPersistent(hologram);
        assertSetPersistent(hologram);
        assertPersist(hologram);

        assertViewPermission(hologram);
        assertVisibleByDefault(hologram);
        assertGetTrackedBy(hologram);
        assertGetLines(hologram);

        assertLineManagement(hologram);

        assertDeleteHologram(hologram);
        assertHologramNotFound(name);
    }

    private void testPlayerHolograms(final Player player) {
        final var name = "service-io-test-player";
        final var hologram = createHologram(name);
        if (hologram == null) return;

        assertHologramsByPlayer(player);
        assertVisibility(hologram, player);

        if (controller.hasCapability(HologramCapability.TEXT_LINES)) {
            assertTextLine(hologram, player);
        } else {
            skip("addTextLine", "TEXT_LINES capability not available");
        }

        assertDeleteHologram(hologram);
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
        if (hologram.isPresent()) pass("getHologram", "found '" + name + "'");
        else fail("getHologram", "hologram '" + name + "' not found after creation");
    }

    private void assertHologramNotFound(final String name) {
        final var hologram = controller.getHologram(name);
        if (hologram.isEmpty()) pass("getHologram (after delete)", "hologram no longer found");
        else fail("getHologram (after delete)", "hologram still found after deletion");
    }

    private void assertHologramsByWorld(final Hologram hologram) {
        final var world = hologram.getWorld();
        final var holograms = controller.getHolograms(world);
        pass("getHolograms(world)", holograms.size() + " hologram(s) in " + world.getName());
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
        if (renamed) {
            if ("service-io-test-renamed".equals(hologram.getName())) {
                pass("setName", "renamed to '" + hologram.getName() + "'");
            } else {
                fail("setName", "setName returned true but getName returned '" + hologram.getName() + "'");
            }
            hologram.setName(original);
        } else {
            fail("setName", "failed to rename hologram");
        }
    }

    private void assertGetLocation(final Hologram hologram) {
        final var loc = hologram.getLocation();
        pass("getLocation", String.format("%.1f, %.1f, %.1f in %s", loc.getX(), loc.getY(), loc.getZ(), hologram.getWorld().getName()));
    }

    private void assertGetWorld(final Hologram hologram) {
        final var world = hologram.getWorld();
        pass("getWorld", world.getName());
    }

    private void assertTeleport(final Hologram hologram) {
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

    private void assertIsPersistent(final Hologram hologram) {
        pass("isPersistent", String.valueOf(hologram.isPersistent()));
    }

    private void assertSetPersistent(final Hologram hologram) {
        final var original = hologram.isPersistent();
        final var changed = hologram.setPersistent(!original);
        if (changed) {
            if (hologram.isPersistent() != original) {
                pass("setPersistent", "changed to " + hologram.isPersistent());
            } else {
                fail("setPersistent", "setPersistent returned true but value did not change");
            }
            hologram.setPersistent(original);
        } else {
            pass("setPersistent", "already " + original + " (no change)");
        }
    }

    private void assertPersist(final Hologram hologram) {
        final var persisted = hologram.persist();
        pass("persist", persisted ? "persisted" : "not persisted");
    }

    private void assertViewPermission(final Hologram hologram) {
        final var originalPermission = hologram.getViewPermission();
        pass("getViewPermission", originalPermission.map(p -> "'" + p + "'").orElse("empty"));

        final var changed = hologram.setViewPermission("service.test.view");
        if (changed) {
            final var retrieved = hologram.getViewPermission();
            if (retrieved.isPresent() && "service.test.view".equals(retrieved.get())) {
                pass("setViewPermission", "set to 'service.test.view'");
            } else {
                fail("setViewPermission", "setViewPermission returned true but getViewPermission returned " + retrieved);
            }
        } else {
            pass("setViewPermission", "no change");
        }

        hologram.setViewPermission(originalPermission.orElse(null));
    }

    private void assertVisibleByDefault(final Hologram hologram) {
        final var original = hologram.isVisibleByDefault();
        pass("isVisibleByDefault", String.valueOf(original));

        final var changed = hologram.setVisibleByDefault(!original);
        if (changed) {
            if (hologram.isVisibleByDefault() != original) {
                pass("setVisibleByDefault", "changed to " + hologram.isVisibleByDefault());
            } else {
                fail("setVisibleByDefault", "setVisibleByDefault returned true but value did not change");
            }
            hologram.setVisibleByDefault(original);
        } else {
            pass("setVisibleByDefault", "already " + original + " (no change)");
        }
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

        final var added = hologram.addViewer(player.getUniqueId());
        if (added) {
            if (hologram.isViewer(player.getUniqueId())) pass("addViewer", "added");
            else fail("addViewer", "addViewer returned true but isViewer is false");
        } else {
            pass("addViewer", "already a viewer");
        }

        pass("isViewer", String.valueOf(hologram.isViewer(player.getUniqueId())));
        pass("getViewers", hologram.getViewers().size() + " viewer(s)");

        final var addedBatch = hologram.addViewers(List.of(player.getUniqueId()));
        pass("addViewers(Collection)", addedBatch ? "added" : "already viewers");

        final var removed = hologram.removeViewer(player.getUniqueId());
        if (removed) {
            if (!hologram.isViewer(player.getUniqueId())) pass("removeViewer", "removed");
            else fail("removeViewer", "removeViewer returned true but isViewer is still true");
        } else {
            pass("removeViewer", "was not a viewer");
        }

        hologram.addViewer(player.getUniqueId());
        final var removedBatch = hologram.removeViewers(List.of(player.getUniqueId()));
        if (removedBatch) {
            if (!hologram.isViewer(player.getUniqueId())) pass("removeViewers(Collection)", "removed");
            else fail("removeViewers(Collection)", "removeViewers returned true but isViewer is still true");
        } else {
            pass("removeViewers(Collection)", "was not a viewer");
        }
    }

    private void assertTextLine(final Hologram hologram, final Player player) {
        if (!controller.hasCapability(HologramCapability.TEXT_LINES)) {
            skip("textLine tests", "TEXT_LINES capability not available");
            return;
        }

        try {
            final var line = hologram.addTextLine();
            pass("addTextLine", "added text line at index " + hologram.getLineIndex(line));

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
            if (lineByIndex.isPresent()) pass("getLine(int)", "type: " + lineByIndex.get().getType());
            else fail("getLine(int)", "no line at index 0");

            final var typedLine = hologram.getLine(0, TextHologramLine.class);
            if (typedLine.isPresent()) pass("getLine(int, Class)", "TextHologramLine at index 0");
            else fail("getLine(int, Class)", "no TextHologramLine at index 0");
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
        pass("TextHologramLine.getWorld", line.getWorld().getName());
    }

    private void assertTextLineSetText(final TextHologramLine line, final Player player) {
        final var text = Component.text("Hello from ServiceIO test!", NamedTextColor.GOLD);
        final var changed = line.setText(text);
        if (changed) {
            final var retrieved = line.getText(player);
            if (retrieved.isPresent()) pass("setText / getText", "text content verified");
            else fail("setText / getText", "setText returned true but getText is empty");
        } else {
            fail("setText", "text was not changed");
        }
    }

    private void assertTextLineUnparsedText(final TextHologramLine line) {
        final var original = line.getUnparsedText();
        pass("getUnparsedText", original.map(t -> "'" + t + "'").orElse("empty"));

        final var changed = line.setUnparsedText("<gold>Unparsed test</gold>");
        if (changed) {
            final var retrieved = line.getUnparsedText();
            if (retrieved.isPresent()) pass("setUnparsedText", "set to '" + retrieved.get() + "'");
            else fail("setUnparsedText", "setUnparsedText returned true but getUnparsedText is empty");
        } else {
            pass("setUnparsedText", "no change");
        }

        line.setUnparsedText(original.orElse(null));
    }

    private void assertTextLineLineWidth(final TextHologramLine line) {
        final var original = line.getLineWidth();
        pass("getLineWidth", String.valueOf(original));

        final var newWidth = original == 200 ? 150 : 200;
        final var changed = line.setLineWidth(newWidth);
        if (changed) {
            if (line.getLineWidth() == newWidth) {
                pass("setLineWidth", "changed to " + newWidth);
            } else {
                fail("setLineWidth", "setLineWidth returned true but getLineWidth returned " + line.getLineWidth());
            }
            line.setLineWidth(original);
        } else {
            pass("setLineWidth", "no change");
        }
    }

    private void assertTextLineBackgroundColor(final TextHologramLine line) {
        final var original = line.getBackgroundColor();
        pass("getBackgroundColor", original.map(Color::toString).orElse("empty"));

        final var changed = line.setBackgroundColor(Color.RED);
        if (changed) {
            final var retrieved = line.getBackgroundColor();
            if (retrieved.isPresent()) pass("setBackgroundColor", "set to " + retrieved.get());
            else fail("setBackgroundColor", "setBackgroundColor returned true but getBackgroundColor is empty");
        } else {
            pass("setBackgroundColor", "no change");
        }

        line.setBackgroundColor(original.orElse(null));
    }

    private void assertTextLineTextOpacity(final TextHologramLine line) {
        final var original = line.getTextOpacity();
        pass("getTextOpacity", String.valueOf(original));

        final var newOpacity = original == 100 ? 50 : 100;
        final var changed = line.setTextOpacity(newOpacity);
        if (changed) {
            if (line.getTextOpacity() == newOpacity) {
                pass("setTextOpacity", "changed to " + newOpacity);
            } else {
                fail("setTextOpacity", "setTextOpacity returned true but getTextOpacity returned " + line.getTextOpacity());
            }
            line.setTextOpacity(original);
        } else {
            pass("setTextOpacity", "no change");
        }
    }

    private void assertTextLineShadowed(final TextHologramLine line) {
        final var original = line.isShadowed();
        pass("isShadowed", String.valueOf(original));

        final var changed = line.setShadowed(!original);
        if (changed) {
            if (line.isShadowed() != original) {
                pass("setShadowed", "changed to " + line.isShadowed());
            } else {
                fail("setShadowed", "setShadowed returned true but value did not change");
            }
            line.setShadowed(original);
        } else {
            pass("setShadowed", "no change");
        }
    }

    private void assertTextLineSeeThrough(final TextHologramLine line) {
        final var original = line.isSeeThrough();
        pass("isSeeThrough", String.valueOf(original));

        final var changed = line.setSeeThrough(!original);
        if (changed) {
            if (line.isSeeThrough() != original) {
                pass("setSeeThrough", "changed to " + line.isSeeThrough());
            } else {
                fail("setSeeThrough", "setSeeThrough returned true but value did not change");
            }
            line.setSeeThrough(original);
        } else {
            pass("setSeeThrough", "no change");
        }
    }

    private void assertTextLineDefaultBackground(final TextHologramLine line) {
        final var original = line.isDefaultBackground();
        pass("isDefaultBackground", String.valueOf(original));

        final var changed = line.setDefaultBackground(!original);
        if (changed) {
            if (line.isDefaultBackground() != original) {
                pass("setDefaultBackground", "changed to " + line.isDefaultBackground());
            } else {
                fail("setDefaultBackground", "setDefaultBackground returned true but value did not change");
            }
            line.setDefaultBackground(original);
        } else {
            pass("setDefaultBackground", "no change");
        }
    }

    private void assertTextLineAlignment(final TextHologramLine line) {
        final var original = line.getAlignment();
        pass("getAlignment", String.valueOf(original));

        final var newAlignment = original == TextAlignment.CENTER ? TextAlignment.LEFT : TextAlignment.CENTER;
        final var changed = line.setAlignment(newAlignment);
        if (changed) {
            if (line.getAlignment() == newAlignment) {
                pass("setAlignment", "changed to " + newAlignment);
            } else {
                fail("setAlignment", "setAlignment returned true but getAlignment returned " + line.getAlignment());
            }
            line.setAlignment(original);
        } else {
            pass("setAlignment", "no change");
        }
    }

    private void assertTextLineViewPermission(final TextHologramLine line) {
        final var original = line.getViewPermission();
        pass("HologramLine.getViewPermission", original.map(p -> "'" + p + "'").orElse("empty"));

        final var changed = line.setViewPermission("service.test.line.view");
        if (changed) {
            final var retrieved = line.getViewPermission();
            if (retrieved.isPresent() && "service.test.line.view".equals(retrieved.get())) {
                pass("HologramLine.setViewPermission", "set to 'service.test.line.view'");
            } else {
                fail("HologramLine.setViewPermission", "setViewPermission returned true but getViewPermission returned " + retrieved);
            }
        } else {
            pass("HologramLine.setViewPermission", "no change");
        }

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
            pass("getLineCount (after adding)", String.valueOf(hologram.getLineCount()));

            final var lines = hologram.getLines().toList();
            pass("getLines (after adding)", lines.size() + " line(s)");

            if (hologram.getLineCount() >= 2) {
                final var swapped = hologram.swapLines(0, 1);
                pass("swapLines(0, 1)", swapped ? "swapped" : "not swapped");

                final var moved = hologram.moveLine(1, 0);
                pass("moveLine(1, 0)", moved ? "moved" : "not moved");
            }

            final var line = hologram.getLine(0);
            if (line.isPresent()) {
                pass("getLine(0)", "type: " + line.get().getType());

                final var typedLine = hologram.getLine(0, TextHologramLine.class);
                if (typedLine.isPresent()) pass("getLine(0, TextHologramLine.class)", "found");
                else fail("getLine(0, TextHologramLine.class)", "not found");

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

    private void assertDeleteHologram(final Hologram hologram) {
        final var deleted = controller.deleteHologram(hologram);
        if (deleted) pass("deleteHologram", "deleted '" + hologram.getName() + "'");
        else fail("deleteHologram", "failed to delete hologram");
    }
}
