package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterCapability;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CharacterTestSuite extends TestSuite<CharacterController> {
    public CharacterTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final CharacterController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getCapabilities", this::testGetCapabilities);
        test("getNPCs", this::testGetNPCs);
        playerAsyncTest("characterLifecycle", this::testCharacterLifecycle);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.toString());
    }

    private void testGetNPCs() {
        final var npcs = controller.getNPCs();
        pass("getNPCs", npcs.size() + " NPC(s)");
    }

    private CompletableFuture<Void> testCharacterLifecycle(final Player player) {
        final var name = "service-io-test";
        final var npc = controller.spawnNPC(name, player.getLocation());
        pass("spawnNPC", "spawned '" + name + "'");

        // CharacterController lookups
        assertGetNPCByName(name);
        assertGetNPCByUUID(npc);
        assertGetNPCs();
        assertGetNPCsByWorld(npc);
        assertGetNPCsByPlayer(player);
        assertGetNPCByEntity(npc);
        assertGetNPCByPlayer(npc, player);

        // Persistable
        assertGetName(npc, name);
        assertIsPersistent(npc);
        assertSetPersistent(npc);
        assertPersist(npc);

        // Character getters
        assertGetDisplayName(npc);
        assertSetDisplayName(npc);
        assertGetType(npc);
        assertIsSpawned(npc, true);

        // Character void setters with verification
        assertSetCollidable(npc);
        assertSetInvulnerable(npc);
        assertSetTablistEntryHidden(npc);

        // Viewable
        assertGetTrackedBy(npc);
        assertGetViewers(npc);
        assertIsTrackedBy(npc, player);
        assertCanSee(npc, player);
        assertAddRemoveViewer(npc, player);
        assertAddRemoveViewers(npc, player);
        assertSetVisibleByDefault(npc);
        assertSetDisplayRange(npc);

        // Character lookAt
        assertLookAtLocation(npc, player);
        assertLookAtEntity(npc, player);

        // Character teleportAsync
        return assertTeleportAsync(npc, player).thenRun(() -> {
            // Character getLocation / getWorld
            assertGetLocation(npc);
            assertGetWorld(npc);

            // Character getEntity (capability-gated)
            assertGetEntity(npc);

            // despawn / isSpawned / respawn / spawn cycle
            assertDespawn(npc);
            assertIsSpawned(npc, false);
            assertRespawn(npc);
            assertIsSpawned(npc, true);
            assertDespawn(npc);
            assertIsSpawned(npc, false);
            assertSpawn(npc, player);
            assertIsSpawned(npc, true);

            // isNPC via entity
            npc.getEntity().ifPresent(this::assertIsNPC);

            // createNPC (unspawned)
            assertCreateNPC();

            // remove
            npc.remove();
            pass("remove", "permanently removed '" + name + "'");
            assertNPCNotFound(name);
        });
    }

    // ---- CharacterController assertions ----

    private void assertGetNPCByName(final String name) {
        final var npc = controller.getNPC(name);
        if (npc.isPresent()) pass("getNPC(name)", "found '" + name + "'");
        else fail("getNPC(name)", "NPC '" + name + "' not found after creation");
    }

    private void assertGetNPCByUUID(final Character<?> npc) {
        final var uuid = npc.getEntity().map(Entity::getUniqueId).orElse(null);
        if (uuid == null) {
            skip("getNPC(uuid)", "no entity UUID available");
            return;
        }
        final var found = controller.getNPC(uuid);
        if (found.isPresent()) pass("getNPC(uuid)", "found by UUID " + uuid);
        else fail("getNPC(uuid)", "not found by UUID " + uuid);
    }

    private void assertNPCNotFound(final String name) {
        final var npc = controller.getNPC(name);
        if (npc.isEmpty()) pass("getNPC (after remove)", "NPC no longer found");
        else fail("getNPC (after remove)", "NPC still found after removal");
    }

    private void assertGetNPCs() {
        final var npcs = controller.getNPCs();
        pass("getNPCs", npcs.size() + " NPC(s)");
    }

    private void assertGetNPCsByWorld(final Character<?> npc) {
        final var world = npc.getWorld();
        if (world != null) {
            final var npcs = controller.getNPCs(world);
            pass("getNPCs(world)", npcs.size() + " NPC(s) in " + world.key().asString());
        } else {
            fail("getNPCs(world)", "NPC has no world");
        }
    }

    private void assertGetNPCsByPlayer(final Player player) {
        final var npcs = controller.getNPCs(player);
        pass("getNPCs(player)", npcs.size() + " NPC(s) for " + player.getName());
    }

    private void assertGetNPCByEntity(final Character<?> npc) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getNPC(entity)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = npc.getEntity().orElse(null);
        if (entity == null) {
            fail("getNPC(entity)", "entity not present despite ACTUAL_ENTITIES capability");
            return;
        }
        final var found = controller.getNPC(entity);
        if (found.isPresent()) pass("getNPC(entity)", "found by entity");
        else fail("getNPC(entity)", "not found by entity");
    }

    private void assertGetNPCByPlayer(final Character<?> npc, final Player player) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getNPC(player)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = npc.getEntity().orElse(null);
        if (entity instanceof final Player npcPlayer) {
            final var found = controller.getNPC(npcPlayer);
            if (found.isPresent()) pass("getNPC(player)", "found NPC player");
            else fail("getNPC(player)", "NPC player not found");
        } else {
            skip("getNPC(player)", "NPC entity is not a Player");
        }
    }

    private void assertCreateNPC() {
        final var created = controller.createNPC("service-io-create-test");
        if (!created.isSpawned()) pass("createNPC", "created unspawned NPC '" + created.getName() + "'");
        else fail("createNPC", "created NPC is unexpectedly spawned");
        created.remove();
        pass("createNPC (cleanup)", "removed created NPC");
    }

    private void assertGetName(final Character<?> npc, final String expected) {
        final var name = npc.getName();
        if (expected.equals(name)) pass("getName", "'" + name + "'");
        else fail("getName", "expected '" + expected + "' but got '" + name + "'");
    }

    private void assertIsPersistent(final Character<?> npc) {
        pass("isPersistent", String.valueOf(npc.isPersistent()));
    }

    private void assertSetPersistent(final Character<?> npc) {
        final var before = npc.isPersistent();
        npc.setPersistent(!before);
        final var after = npc.isPersistent();
        if (after != before) pass("setPersistent", "changed from " + before + " to " + after);
        else fail("setPersistent", "value did not change, still " + after);
        npc.setPersistent(before);
    }

    private void assertPersist(final Character<?> npc) {
        final var result = npc.persist();
        if (result) pass("persist", "persisted successfully");
        else fail("persist", "persist returned false");
    }

    private void assertGetDisplayName(final Character<?> npc) {
        pass("getDisplayName", npc.getDisplayName().toString());
    }

    private void assertSetDisplayName(final Character<?> npc) {
        final var before = npc.getDisplayName();
        final var newName = Component.text("ServiceIO Test NPC", NamedTextColor.GOLD);
        npc.setDisplayName(newName);
        final var after = npc.getDisplayName();
        if (!after.equals(before)) pass("setDisplayName", "changed display name");
        else fail("setDisplayName", "display name did not change");
        npc.setDisplayName(before);
    }

    private void assertGetType(final Character<?> npc) {
        final var type = npc.getType();
        if (type == EntityType.PLAYER) pass("getType", "PLAYER");
        else pass("getType", type.toString());
    }

    private void assertIsSpawned(final Character<?> npc, final boolean expected) {
        final var spawned = npc.isSpawned();
        if (spawned == expected) pass("isSpawned", String.valueOf(spawned));
        else fail("isSpawned", "expected " + expected + " but got " + spawned);
    }

    private void assertSetCollidable(final Character<?> npc) {
        final var before = npc.isCollidable();
        npc.setCollidable(!before);
        final var after = npc.isCollidable();
        if (after != before) pass("setCollidable", "changed from " + before + " to " + after);
        else fail("setCollidable", "value did not change, still " + after);
        npc.setCollidable(before);
    }

    private void assertSetInvulnerable(final Character<?> npc) {
        if (!controller.hasCapability(CharacterCapability.HEALTH)) {
            skip("setInvulnerable", "requires HEALTH capability");
            return;
        }
        final var before = npc.isInvulnerable();
        npc.setInvulnerable(!before);
        final var after = npc.isInvulnerable();
        if (after != before) pass("setInvulnerable", "changed from " + before + " to " + after);
        else fail("setInvulnerable", "value did not change, still " + after);
        npc.setInvulnerable(before);
    }

    private void assertSetTablistEntryHidden(final Character<?> npc) {
        final var before = npc.isTablistEntryHidden();
        npc.setTablistEntryHidden(!before);
        final var after = npc.isTablistEntryHidden();
        if (after != before) pass("setTablistEntryHidden", "changed from " + before + " to " + after);
        else fail("setTablistEntryHidden", "value did not change, still " + after);
        npc.setTablistEntryHidden(before);
    }

    private void assertGetTrackedBy(final Character<?> npc) {
        final var tracked = npc.getTrackedBy();
        pass("getTrackedBy", tracked.size() + " player(s)");
    }

    private void assertGetViewers(final Character<?> npc) {
        final var viewers = npc.getViewers();
        pass("getViewers", viewers.size() + " viewer(s)");
    }

    private void assertIsTrackedBy(final Character<?> npc, final Player player) {
        final var tracked = npc.isTrackedBy(player);
        pass("isTrackedBy", String.valueOf(tracked));
    }

    private void assertCanSee(final Character<?> npc, final Player player) {
        final var canSee = npc.canSee(player);
        pass("canSee", String.valueOf(canSee));
    }

    private void assertAddRemoveViewer(final Character<?> npc, final Player player) {
        final var added = npc.addViewer(player);
        if (added) {
            final var viewers = npc.getViewers();
            if (viewers.contains(player)) pass("addViewer", "added and verified in getViewers()");
            else fail("addViewer", "addViewer returned true but player not in getViewers()");
        } else {
            pass("addViewer", "returned false (player may already be a viewer)");
        }

        final var removed = npc.removeViewer(player);
        if (removed) {
            final var viewers = npc.getViewers();
            if (!viewers.contains(player)) pass("removeViewer", "removed and verified not in getViewers()");
            else fail("removeViewer", "removeViewer returned true but player still in getViewers()");
        } else {
            pass("removeViewer", "returned false (player may not have been a viewer)");
        }
    }

    private void assertAddRemoveViewers(final Character<?> npc, final Player player) {
        final var players = List.of(player);
        final var added = npc.addViewers(players);
        if (added) pass("addViewers", "added viewer(s)");
        else pass("addViewers", "returned false (viewer(s) may already exist)");

        final var removed = npc.removeViewers(players);
        if (removed) pass("removeViewers", "removed viewer(s)");
        else pass("removeViewers", "returned false (viewer(s) may not have existed)");
    }

    private void assertSetVisibleByDefault(final Character<?> npc) {
        final var before = npc.isVisibleByDefault();
        npc.setVisibleByDefault(!before);
        final var after = npc.isVisibleByDefault();
        if (after != before) pass("setVisibleByDefault", "changed from " + before + " to " + after);
        else fail("setVisibleByDefault", "value did not change, still " + after);
        npc.setVisibleByDefault(before);
    }

    private void assertSetDisplayRange(final Character<?> npc) {
        final var before = npc.getDisplayRange();
        final var newRange = before + 10.0;
        npc.setDisplayRange(newRange);
        final var after = npc.getDisplayRange();
        if (Double.compare(after, before) != 0) pass("setDisplayRange", "changed from " + before + " to " + after);
        else fail("setDisplayRange", "value did not change, still " + after);
        npc.setDisplayRange(before);
    }

    private void assertLookAtLocation(final Character<?> npc, final Player player) {
        final var target = player.getLocation().add(5, 0, 5);
        npc.lookAt(target);
        pass("lookAt(location)", String.format("looked at %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
    }

    private void assertLookAtEntity(final Character<?> npc, final Player player) {
        npc.lookAt(player);
        pass("lookAt(entity)", "looked at " + player.getName());
    }

    private CompletableFuture<Void> assertTeleportAsync(final Character<?> npc, final Player player) {
        final var target = player.getLocation().add(0, 2, 0);
        return npc.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass("teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail("teleportAsync", "teleport returned false");
        });
    }

    private void assertGetLocation(final Character<?> npc) {
        final var location = npc.getLocation();
        if (location != null)
            pass("getLocation", String.format("%.1f, %.1f, %.1f", location.getX(), location.getY(), location.getZ()));
        else fail("getLocation", "location is null");
    }

    private void assertGetWorld(final Character<?> npc) {
        final var world = npc.getWorld();
        if (world != null) pass("getWorld", world.key().asString());
        else fail("getWorld", "world is null");
    }

    private void assertGetEntity(final Character<?> npc) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getEntity", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = npc.getEntity();
        if (entity.isPresent()) pass("getEntity", entity.get().getType().toString());
        else fail("getEntity", "entity not present despite ACTUAL_ENTITIES capability");
    }

    private void assertDespawn(final Character<?> npc) {
        final var despawned = npc.despawn();
        if (despawned) {
            if (!npc.isSpawned()) pass("despawn", "despawned NPC");
            else fail("despawn", "despawn returned true but isSpawned() is still true");
        } else {
            fail("despawn", "failed to despawn NPC");
        }
    }

    private void assertRespawn(final Character<?> npc) {
        final var respawned = npc.respawn();
        if (respawned) {
            if (npc.isSpawned()) pass("respawn", "respawned NPC");
            else fail("respawn", "respawn returned true but isSpawned() is still false");
        } else {
            fail("respawn", "failed to respawn NPC");
        }
    }

    private void assertSpawn(final Character<?> npc, final Player player) {
        final var location = player.getLocation();
        final var spawned = npc.spawn(location);
        if (spawned) {
            if (npc.isSpawned()) pass("spawn(location)", "spawned at player location");
            else fail("spawn(location)", "spawn returned true but isSpawned() is still false");
        } else {
            fail("spawn(location)", "failed to spawn NPC");
        }
    }

    private void assertIsNPC(final Entity entity) {
        if (controller.isNPC(entity)) pass("isNPC", "entity is an NPC");
        else fail("isNPC", "entity is not recognized as NPC");
    }
}
