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

        return lifecycle(
                () -> assertGetNPCByName(name),
                () -> assertGetNPCByUUID(npc),
                this::assertGetNPCs,
                () -> assertGetNPCsByWorld(npc),
                () -> assertGetNPCsByPlayer(player),
                () -> assertGetNPCByEntity(npc),
                () -> assertGetNPCByPlayer(npc, player),
                () -> assertGetName(npc, name),
                () -> assertIsPersistent(npc),
                () -> assertSetPersistent(npc),
                () -> assertPersist(npc),
                () -> assertGetDisplayName(npc),
                () -> assertSetDisplayName(npc),
                () -> assertGetType(npc),
                () -> assertIsSpawned(npc, true),
                () -> assertSetCollidable(npc),
                () -> assertSetInvulnerable(npc),
                () -> assertSetTablistEntryHidden(npc),
                () -> assertGetTrackedBy(npc),
                () -> assertGetViewers(npc),
                () -> assertIsTrackedBy(npc, player),
                () -> assertCanSee(npc, player),
                () -> assertAddRemoveViewer(npc, player),
                () -> assertAddRemoveViewers(npc, player),
                () -> assertSetVisibleByDefault(npc),
                () -> assertSetDisplayRange(npc),
                () -> assertLookAtLocation(npc, player),
                () -> assertLookAtEntity(npc, player)
        ).thenCompose(ignored -> lifecycleAsync(
                () -> assertTeleportAsync(npc, player)
        )).thenCompose(ignored -> lifecycle(
                () -> assertGetLocation(npc),
                () -> assertGetWorld(npc),
                () -> assertGetEntity(npc),
                () -> assertDespawn(npc),
                () -> assertIsSpawned(npc, false),
                () -> assertRespawn(npc),
                () -> assertIsSpawned(npc, true),
                () -> assertDespawn(npc),
                () -> assertIsSpawned(npc, false),
                () -> assertSpawn(npc, player),
                () -> assertIsSpawned(npc, true),
                () -> npc.getEntity().ifPresent(this::assertIsNPC),
                this::assertCreateNPC,
                () -> {
                    npc.remove();
                    pass("remove", "permanently removed '" + name + "'");
                },
                () -> assertNPCNotFound(name)
        ));
    }

    // ---- CharacterController assertions ----

    private void assertGetNPCByName(final String name) {
        final var npc = controller.getNPC(name);
        assertTrue(npc.isPresent(), "getNPC(name)");
    }

    private void assertGetNPCByUUID(final Character npc) {
        final var uuid = npc.getEntity().map(Entity::getUniqueId).orElse(null);
        if (uuid == null) {
            skip("getNPC(uuid)", "no entity UUID available");
            return;
        }
        final var found = controller.getNPC(uuid);
        assertTrue(found.isPresent(), "getNPC(uuid)");
    }

    private void assertNPCNotFound(final String name) {
        final var npc = controller.getNPC(name);
        assertTrue(npc.isEmpty(), "getNPC (after remove)");
    }

    private void assertGetNPCs() {
        final var npcs = controller.getNPCs();
        pass("getNPCs", npcs.size() + " NPC(s)");
    }

    private void assertGetNPCsByWorld(final Character npc) {
        npc.getWorld().ifPresentOrElse(world -> {
            final var npcs = controller.getNPCs(world);
            pass("getNPCs(world)", npcs.size() + " NPC(s) in " + world.key().asString());
        }, () -> fail("getNPCs(world)", "NPC has no world"));
    }

    private void assertGetNPCsByPlayer(final Player player) {
        final var npcs = controller.getNPCs(player);
        pass("getNPCs(player)", npcs.size() + " NPC(s) for " + player.getName());
    }

    private void assertGetNPCByEntity(final Character npc) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            assertTrue(npc.getEntity().isEmpty(), "#getEntity().isEmpty()");
            skip("getNPC(entity)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        npc.getEntity().ifPresentOrElse(entity -> {
            final var found = controller.getNPC(entity);
            assertTrue(found.isPresent(), "getNPC(entity)");
        }, () -> {
            fail("getNPC(entity)", "entity not present despite ACTUAL_ENTITIES capability");
        });
    }

    private void assertGetNPCByPlayer(final Character npc, final Player player) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getNPC(player)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = npc.getEntity().orElse(null);
        if (entity instanceof final Player npcPlayer) {
            final var found = controller.getNPC(npcPlayer);
            assertTrue(found.isPresent(), "getNPC(player)");
        } else {
            skip("getNPC(player)", "NPC entity is not a Player");
        }
    }

    private void assertCreateNPC() {
        final var created = controller.createNPC("service-io-create-test");
        assertFalse(created.isSpawned(), "createNPC");
        created.remove();
        pass("createNPC (cleanup)", "removed created NPC");
    }

    private void assertGetName(final Character npc, final String expected) {
        final var name = npc.getName();
        assertEquals(expected, name, "getName");
    }

    private void assertIsPersistent(final Character npc) {
        pass("isPersistent", String.valueOf(npc.isPersistent()));
    }

    private void assertSetPersistent(final Character npc) {
        final var before = npc.isPersistent();
        final var changed = npc.setPersistent(!before);
        final var after = npc.isPersistent();
        if (changed && after != before) pass("setPersistent", "changed from " + before + " to " + after);
        else fail("setPersistent", "value did not change, still " + after);
        npc.setPersistent(before);
    }

    private void assertPersist(final Character npc) {
        final var result = npc.persist();
        assertTrue(result, "persist");
    }

    private void assertGetDisplayName(final Character npc) {
        pass("getDisplayName", npc.getDisplayName().toString());
    }

    private void assertSetDisplayName(final Character npc) {
        final var before = npc.getDisplayName();
        final var newName = Component.text("ServiceIO Test NPC", NamedTextColor.GOLD);
        npc.setDisplayName(newName);
        final var after = npc.getDisplayName();
        if (!after.equals(before)) pass("setDisplayName", "changed display name");
        else fail("setDisplayName", "display name did not change");
        npc.setDisplayName(before);
    }

    private void assertGetType(final Character npc) {
        final var type = npc.getType();
        if (type == EntityType.PLAYER) pass("getType", "PLAYER");
        else pass("getType", type.toString());
    }

    private void assertIsSpawned(final Character npc, final boolean expected) {
        final var spawned = npc.isSpawned();
        assertEquals(expected, spawned, "isSpawned");
    }

    private void assertSetCollidable(final Character npc) {
        final var before = npc.isCollidable();
        npc.setCollidable(!before);
        final var after = npc.isCollidable();
        if (after != before) pass("setCollidable", "changed from " + before + " to " + after);
        else fail("setCollidable", "value did not change, still " + after);
        npc.setCollidable(before);
    }

    private void assertSetInvulnerable(final Character npc) {
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

    private void assertSetTablistEntryHidden(final Character npc) {
        final var before = npc.isTablistEntryHidden();
        npc.setTablistEntryHidden(!before);
        final var after = npc.isTablistEntryHidden();
        if (after != before) pass("setTablistEntryHidden", "changed from " + before + " to " + after);
        else fail("setTablistEntryHidden", "value did not change, still " + after);
        npc.setTablistEntryHidden(before);
    }

    private void assertGetTrackedBy(final Character npc) {
        final var tracked = npc.getTrackedBy();
        pass("getTrackedBy", tracked.size() + " player(s)");
    }

    private void assertGetViewers(final Character npc) {
        final var viewers = npc.getViewers();
        pass("getViewers", viewers.size() + " viewer(s)");
    }

    private void assertIsTrackedBy(final Character npc, final Player player) {
        final var tracked = npc.isTrackedBy(player);
        pass("isTrackedBy", String.valueOf(tracked));
    }

    private void assertCanSee(final Character npc, final Player player) {
        final var canSee = npc.canSee(player);
        pass("canSee", String.valueOf(canSee));
    }

    private void assertAddRemoveViewer(final Character npc, final Player player) {
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

    private void assertAddRemoveViewers(final Character npc, final Player player) {
        final var players = List.of(player);
        final var added = npc.addViewers(players);
        if (added) pass("addViewers", "added viewer(s)");
        else pass("addViewers", "returned false (viewer(s) may already exist)");

        final var removed = npc.removeViewers(players);
        if (removed) pass("removeViewers", "removed viewer(s)");
        else pass("removeViewers", "returned false (viewer(s) may not have existed)");
    }

    private void assertSetVisibleByDefault(final Character npc) {
        final var before = npc.isVisibleByDefault();
        final var changed = npc.setVisibleByDefault(!before);
        final var after = npc.isVisibleByDefault();
        if (changed && after != before) pass("setVisibleByDefault", "changed from " + before + " to " + after);
        else fail("setVisibleByDefault", "value did not change, still " + after);
        npc.setVisibleByDefault(before);
    }

    private void assertSetDisplayRange(final Character npc) {
        final var before = npc.getDisplayRange();
        final var newRange = before + 10.0;
        npc.setDisplayRange(newRange);
        final var after = npc.getDisplayRange();
        if (Double.compare(after, before) != 0) pass("setDisplayRange", "changed from " + before + " to " + after);
        else fail("setDisplayRange", "value did not change, still " + after);
        npc.setDisplayRange(before);
    }

    private void assertLookAtLocation(final Character npc, final Player player) {
        final var target = player.getLocation().add(5, 0, 5);
        npc.lookAt(target);
        pass("lookAt(location)", String.format("looked at %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
    }

    private void assertLookAtEntity(final Character npc, final Player player) {
        npc.lookAt(player);
        pass("lookAt(entity)", "looked at " + player.getName());
    }

    private CompletableFuture<Void> assertTeleportAsync(final Character npc, final Player player) {
        final var target = player.getLocation().add(0, 2, 0);
        return npc.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass("teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail("teleportAsync", "teleport returned false");
        });
    }

    private void assertGetLocation(final Character npc) {
        npc.getLocation().ifPresentOrElse(location -> {
            pass("getLocation", String.format("%.1f, %.1f, %.1f", location.getX(), location.getY(), location.getZ()));
        }, () -> fail("getLocation", "location is null"));
    }

    private void assertGetWorld(final Character npc) {
        npc.getWorld().ifPresentOrElse(world -> {
            pass("getWorld", world.key().asString());
        }, () -> fail("getWorld", "world is null"));
    }

    private void assertGetEntity(final Character npc) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getEntity", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = npc.getEntity();
        if (entity.isPresent()) pass("getEntity", entity.get().getType().toString());
        else fail("getEntity", "entity not present despite ACTUAL_ENTITIES capability");
    }

    private void assertDespawn(final Character npc) {
        final var despawned = npc.despawn();
        if (despawned) {
            if (!npc.isSpawned()) pass("despawn", "despawned NPC");
            else fail("despawn", "despawn returned true but isSpawned() is still true");
        } else {
            fail("despawn", "failed to despawn NPC");
        }
    }

    private void assertRespawn(final Character npc) {
        final var respawned = npc.respawn();
        if (respawned) {
            if (npc.isSpawned()) pass("respawn", "respawned NPC");
            else fail("respawn", "respawn returned true but isSpawned() is still false");
        } else {
            fail("respawn", "failed to respawn NPC");
        }
    }

    private void assertSpawn(final Character npc, final Player player) {
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
        assertTrue(controller.isNPC(entity), "isNPC");
    }
}
