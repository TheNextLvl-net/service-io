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
        test("getCharacters", this::testGetCharacters);
        playerAsyncTest("characterLifecycle", this::testCharacterLifecycle);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.toString());
    }

    private void testGetCharacters() {
        final var characters = controller.getCharacters();
        pass("getCharacters", characters.size() + " character(s)");
    }

    private CompletableFuture<Void> testCharacterLifecycle(final Player player) {
        final var name = "service-io-test";
        final var character = controller.spawnCharacter(name, player.getLocation());
        pass("spawnCharacter", "spawned '" + name + "'");

        return lifecycle(
                () -> assertGetCharacterByName(name),
                () -> assertGetCharacterByUUID(character),
                this::assertGetCharacters,
                () -> assertGetCharactersByWorld(character),
                () -> assertGetCharactersByPlayer(player),
                () -> assertGetCharacterByEntity(character),
                () -> assertGetCharacterByPlayer(character, player),
                () -> assertGetName(character, name),
                () -> assertIsPersistent(character),
                () -> assertSetPersistent(character),
                () -> assertPersist(character),
                () -> assertGetDisplayName(character),
                () -> assertSetDisplayName(character),
                () -> assertGetType(character),
                () -> assertIsSpawned(character, true),
                () -> assertSetCollidable(character),
                () -> assertSetInvulnerable(character),
                () -> assertSetTablistEntryHidden(character),
                () -> assertGetTrackedBy(character),
                () -> assertGetViewers(character),
                () -> assertIsTrackedBy(character, player),
                () -> assertCanSee(character, player),
                () -> assertAddRemoveViewer(character, player),
                () -> assertAddRemoveViewers(character, player),
                () -> assertSetVisibleByDefault(character),
                () -> assertSetDisplayRange(character),
                () -> assertLookAtLocation(character, player),
                () -> assertLookAtEntity(character, player)
        ).thenCompose(ignored -> lifecycleAsync(
                () -> assertTeleportAsync(character, player)
        )).thenCompose(ignored -> lifecycle(
                () -> assertGetLocation(character),
                () -> assertGetWorld(character),
                () -> assertGetEntity(character),
                () -> assertDespawn(character),
                () -> assertIsSpawned(character, false),
                () -> assertRespawn(character),
                () -> assertIsSpawned(character, true),
                () -> assertDespawn(character),
                () -> assertIsSpawned(character, false),
                () -> assertSpawn(character, player),
                () -> assertIsSpawned(character, true),
                () -> character.getEntity().ifPresent(this::assertIsCharacter),
                this::assertCreateCharacter,
                () -> {
                    character.remove();
                    pass("remove", "permanently removed '" + name + "'");
                },
                () -> assertCharacterNotFound(name)
        ));
    }

    // ---- CharacterController assertions ----

    private void assertGetCharacterByName(final String name) {
        final var character = controller.getCharacter(name);
        assertTrue(character.isPresent(), "getCharacter(name)");
    }

    private void assertGetCharacterByUUID(final Character character) {
        final var uuid = character.getEntity().map(Entity::getUniqueId).orElse(null);
        if (uuid == null) {
            skip("getCharacter(uuid)", "no entity UUID available");
            return;
        }
        final var found = controller.getCharacter(uuid);
        assertTrue(found.isPresent(), "getCharacter(uuid)");
    }

    private void assertCharacterNotFound(final String name) {
        final var character = controller.getCharacter(name);
        assertTrue(character.isEmpty(), "getCharacter (after remove)");
    }

    private void assertGetCharacters() {
        final var characters = controller.getCharacters();
        pass("getCharacters", characters.size() + " character(s)");
    }

    private void assertGetCharactersByWorld(final Character character) {
        character.getWorld().ifPresentOrElse(world -> {
            final var characters = controller.getCharacters(world);
            pass("getCharacters(world)", characters.size() + " character(s) in " + world.key().asString());
        }, () -> fail("getCharacters(world)", "character has no world"));
    }

    private void assertGetCharactersByPlayer(final Player player) {
        final var characters = controller.getCharacters(player);
        pass("getCharacters(player)", characters.size() + " character(s) for " + player.getName());
    }

    private void assertGetCharacterByEntity(final Character character) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            assertTrue(character.getEntity().isEmpty(), "#getEntity().isEmpty()");
            skip("getCharacter(entity)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        character.getEntity().ifPresentOrElse(entity -> {
            final var found = controller.getCharacter(entity);
            assertTrue(found.isPresent(), "getCharacter(entity)");
        }, () -> {
            fail("getCharacter(entity)", "entity not present despite ACTUAL_ENTITIES capability");
        });
    }

    private void assertGetCharacterByPlayer(final Character character, final Player player) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getCharacter(player)", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = character.getEntity().orElse(null);
        if (entity instanceof final Player characterPlayer) {
            final var found = controller.getCharacter(characterPlayer);
            assertTrue(found.isPresent(), "getCharacter(player)");
        } else {
            skip("getCharacter(player)", "character entity is not a Player");
        }
    }

    private void assertCreateCharacter() {
        final var created = controller.createCharacter("service-io-create-test");
        assertFalse(created.isSpawned(), "createCharacter");
        created.remove();
        pass("createCharacter (cleanup)", "removed created character");
    }

    private void assertGetName(final Character character, final String expected) {
        final var name = character.getName();
        assertEquals(expected, name, "getName");
    }

    private void assertIsPersistent(final Character character) {
        pass("isPersistent", String.valueOf(character.isPersistent()));
    }

    private void assertSetPersistent(final Character character) {
        final var before = character.isPersistent();
        final var changed = character.setPersistent(!before);
        final var after = character.isPersistent();
        if (changed && after != before) pass("setPersistent", "changed from " + before + " to " + after);
        else fail("setPersistent", "value did not change, still " + after);
        character.setPersistent(before);
    }

    private void assertPersist(final Character character) {
        final var result = character.persist();
        assertTrue(result, "persist");
    }

    private void assertGetDisplayName(final Character character) {
        pass("getDisplayName", character.getDisplayName().toString());
    }

    private void assertSetDisplayName(final Character character) {
        final var before = character.getDisplayName();
        final var newName = Component.text("ServiceIO Test Character", NamedTextColor.GOLD);
        character.setDisplayName(newName);
        final var after = character.getDisplayName();
        if (!after.equals(before)) pass("setDisplayName", "changed display name");
        else fail("setDisplayName", "display name did not change");
        character.setDisplayName(before);
    }

    private void assertGetType(final Character character) {
        final var type = character.getType();
        if (type == EntityType.PLAYER) pass("getType", "PLAYER");
        else pass("getType", type.toString());
    }

    private void assertIsSpawned(final Character character, final boolean expected) {
        final var spawned = character.isSpawned();
        assertEquals(expected, spawned, "isSpawned");
    }

    private void assertSetCollidable(final Character character) {
        final var before = character.isCollidable();
        character.setCollidable(!before);
        final var after = character.isCollidable();
        if (after != before) pass("setCollidable", "changed from " + before + " to " + after);
        else fail("setCollidable", "value did not change, still " + after);
        character.setCollidable(before);
    }

    private void assertSetInvulnerable(final Character character) {
        if (!controller.hasCapability(CharacterCapability.HEALTH)) {
            skip("setInvulnerable", "requires HEALTH capability");
            return;
        }
        final var before = character.isInvulnerable();
        character.setInvulnerable(!before);
        final var after = character.isInvulnerable();
        if (after != before) pass("setInvulnerable", "changed from " + before + " to " + after);
        else fail("setInvulnerable", "value did not change, still " + after);
        character.setInvulnerable(before);
    }

    private void assertSetTablistEntryHidden(final Character character) {
        final var before = character.isTablistEntryHidden();
        character.setTablistEntryHidden(!before);
        final var after = character.isTablistEntryHidden();
        if (after != before) pass("setTablistEntryHidden", "changed from " + before + " to " + after);
        else fail("setTablistEntryHidden", "value did not change, still " + after);
        character.setTablistEntryHidden(before);
    }

    private void assertGetTrackedBy(final Character character) {
        final var tracked = character.getTrackedBy();
        pass("getTrackedBy", tracked.size() + " player(s)");
    }

    private void assertGetViewers(final Character character) {
        final var viewers = character.getViewers();
        pass("getViewers", viewers.size() + " viewer(s)");
    }

    private void assertIsTrackedBy(final Character character, final Player player) {
        final var tracked = character.isTrackedBy(player);
        pass("isTrackedBy", String.valueOf(tracked));
    }

    private void assertCanSee(final Character character, final Player player) {
        final var canSee = character.canSee(player);
        pass("canSee", String.valueOf(canSee));
    }

    private void assertAddRemoveViewer(final Character character, final Player player) {
        final var added = character.addViewer(player);
        if (added) {
            final var viewers = character.getViewers();
            if (viewers.contains(player)) pass("addViewer", "added and verified in getViewers()");
            else fail("addViewer", "addViewer returned true but player not in getViewers()");
        } else {
            pass("addViewer", "returned false (player may already be a viewer)");
        }

        final var removed = character.removeViewer(player);
        if (removed) {
            final var viewers = character.getViewers();
            if (!viewers.contains(player)) pass("removeViewer", "removed and verified not in getViewers()");
            else fail("removeViewer", "removeViewer returned true but player still in getViewers()");
        } else {
            pass("removeViewer", "returned false (player may not have been a viewer)");
        }
    }

    private void assertAddRemoveViewers(final Character character, final Player player) {
        final var players = List.of(player);
        final var added = character.addViewers(players);
        if (added) pass("addViewers", "added viewer(s)");
        else pass("addViewers", "returned false (viewer(s) may already exist)");

        final var removed = character.removeViewers(players);
        if (removed) pass("removeViewers", "removed viewer(s)");
        else pass("removeViewers", "returned false (viewer(s) may not have existed)");
    }

    private void assertSetVisibleByDefault(final Character character) {
        final var before = character.isVisibleByDefault();
        final var changed = character.setVisibleByDefault(!before);
        final var after = character.isVisibleByDefault();
        if (changed && after != before) pass("setVisibleByDefault", "changed from " + before + " to " + after);
        else fail("setVisibleByDefault", "value did not change, still " + after);
        character.setVisibleByDefault(before);
    }

    private void assertSetDisplayRange(final Character character) {
        final var before = character.getDisplayRange();
        final var newRange = before + 10.0;
        character.setDisplayRange(newRange);
        final var after = character.getDisplayRange();
        if (Double.compare(after, before) != 0) pass("setDisplayRange", "changed from " + before + " to " + after);
        else fail("setDisplayRange", "value did not change, still " + after);
        character.setDisplayRange(before);
    }

    private void assertLookAtLocation(final Character character, final Player player) {
        final var target = player.getLocation().add(5, 0, 5);
        character.lookAt(target);
        pass("lookAt(location)", String.format("looked at %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
    }

    private void assertLookAtEntity(final Character character, final Player player) {
        character.lookAt(player);
        pass("lookAt(entity)", "looked at " + player.getName());
    }

    private CompletableFuture<Void> assertTeleportAsync(final Character character, final Player player) {
        final var target = player.getLocation().add(0, 2, 0);
        return character.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass("teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail("teleportAsync", "teleport returned false");
        });
    }

    private void assertGetLocation(final Character character) {
        character.getLocation().ifPresentOrElse(location -> {
            pass("getLocation", String.format("%.1f, %.1f, %.1f", location.getX(), location.getY(), location.getZ()));
        }, () -> fail("getLocation", "location is null"));
    }

    private void assertGetWorld(final Character character) {
        character.getWorld().ifPresentOrElse(world -> {
            pass("getWorld", world.key().asString());
        }, () -> fail("getWorld", "world is null"));
    }

    private void assertGetEntity(final Character character) {
        if (!controller.hasCapability(CharacterCapability.ACTUAL_ENTITIES)) {
            skip("getEntity", "requires ACTUAL_ENTITIES capability");
            return;
        }
        final var entity = character.getEntity();
        if (entity.isPresent()) pass("getEntity", entity.get().getType().toString());
        else fail("getEntity", "entity not present despite ACTUAL_ENTITIES capability");
    }

    private void assertDespawn(final Character character) {
        final var despawned = character.despawn();
        if (despawned) {
            if (!character.isSpawned()) pass("despawn", "despawned character");
            else fail("despawn", "despawn returned true but isSpawned() is still true");
        } else {
            fail("despawn", "failed to despawn character");
        }
    }

    private void assertRespawn(final Character character) {
        final var respawned = character.respawn();
        if (respawned) {
            if (character.isSpawned()) pass("respawn", "respawned character");
            else fail("respawn", "respawn returned true but isSpawned() is still false");
        } else {
            fail("respawn", "failed to respawn character");
        }
    }

    private void assertSpawn(final Character character, final Player player) {
        final var location = player.getLocation();
        final var spawned = character.spawn(location);
        if (spawned) {
            if (character.isSpawned()) pass("spawn(location)", "spawned at player location");
            else fail("spawn(location)", "spawn returned true but isSpawned() is still false");
        } else {
            fail("spawn(location)", "failed to spawn character");
        }
    }

    private void assertIsCharacter(final Entity entity) {
        assertTrue(controller.isCharacter(entity), "isCharacter");
    }
}
