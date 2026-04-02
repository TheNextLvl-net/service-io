package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.character.Character;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class CharacterTestSuite extends TestSuite<CharacterController> {
    public CharacterTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final CharacterController controller) {
        super(plugin, source, controller);
    }

    @Override
    protected void setup() {
        test("getCapabilities", this::testGetCapabilities);
        test("getNPCs", this::testGetNPCs);
        playerTest("characterLifecycle", this::testCharacterLifecycle);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.toString());
    }

    private void testGetNPCs() {
        final var npcs = controller.getNPCs();
        pass("getNPCs", npcs.size() + " NPC(s)");
    }

    private void testCharacterLifecycle(final Player player) {
        final var name = "service-io-test";
        final var npc = controller.spawnNPC(name, player.getLocation());
        pass("spawnNPC", "spawned '" + name + "'");

        assertNPCFound(name);
        assertGetNPCs();
        assertGetNPCsByWorld(npc);

        assertGetDisplayName(npc);
        assertSetDisplayName(npc);

        assertGetType(npc);
        assertIsSpawned(npc, true);
        assertIsCollidable(npc);
        assertSetCollidable(npc);
        assertIsTablistEntryHidden(npc);
        assertSetTablistEntryHidden(npc);
        assertIsPersistent(npc);
        assertSetPersistent(npc);
        assertTeleportAsync(npc, player);

        assertDespawn(npc);
        assertIsSpawned(npc, false);
        assertRespawn(npc);
        assertIsSpawned(npc, true);

        npc.getEntity().ifPresent(this::assertIsNPC);
        assertGetNPCsByPlayer(player);

        npc.remove();
        pass("remove", "permanently removed '" + name + "'");
        assertNPCNotFound(name);
    }

    private void assertNPCFound(final String name) {
        final var npc = controller.getNPC(name);
        if (npc.isPresent()) pass("getNPC", "found '" + name + "'");
        else fail("getNPC", "NPC '" + name + "' not found after creation");
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
            pass("getNPCs(world)", npcs.size() + " NPC(s) in " + world.getName());
        } else {
            fail("getNPCs(world)", "NPC has no world");
        }
    }

    private void assertGetNPCsByPlayer(final Player player) {
        final var npcs = controller.getNPCs(player);
        pass("getNPCs(player)", npcs.size() + " NPC(s) for " + player.getName());
    }

    private void assertGetDisplayName(final Character<?> npc) {
        pass("getDisplayName", npc.getDisplayName().toString());
    }

    private void assertSetDisplayName(final Character<?> npc) {
        final var original = npc.getDisplayName();
        final var newName = Component.text("ServiceIO Test NPC", NamedTextColor.GOLD);
        npc.setDisplayName(newName);
        pass("setDisplayName", "changed display name");
        npc.setDisplayName(original);
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

    private void assertIsCollidable(final Character<?> npc) {
        pass("isCollidable", String.valueOf(npc.isCollidable()));
    }

    private void assertSetCollidable(final Character<?> npc) {
        final var original = npc.isCollidable();
        npc.setCollidable(!original);
        pass("setCollidable", "changed to " + npc.isCollidable());
        npc.setCollidable(original);
    }

    private void assertIsTablistEntryHidden(final Character<?> npc) {
        pass("isTablistEntryHidden", String.valueOf(npc.isTablistEntryHidden()));
    }

    private void assertSetTablistEntryHidden(final Character<?> npc) {
        final var original = npc.isTablistEntryHidden();
        npc.setTablistEntryHidden(!original);
        pass("setTablistEntryHidden", "changed to " + npc.isTablistEntryHidden());
        npc.setTablistEntryHidden(original);
    }

    private void assertIsPersistent(final Character<?> npc) {
        pass("isPersistent", String.valueOf(npc.isPersistent()));
    }

    private void assertSetPersistent(final Character<?> npc) {
        final var original = npc.isPersistent();
        npc.setPersistent(!original);
        pass("setPersistent", "changed to " + npc.isPersistent());
        npc.setPersistent(original);
    }

    private void assertTeleportAsync(final Character<?> npc, final Player player) {
        final var target = player.getLocation().add(0, 2, 0);
        npc.teleportAsync(target).thenAccept(success -> {
            if (success)
                pass("teleportAsync", String.format("moved to %.1f, %.1f, %.1f", target.getX(), target.getY(), target.getZ()));
            else fail("teleportAsync", "teleport returned false");
        }).exceptionally(throwable -> {
            fail("teleportAsync", throwable.getMessage());
            return null;
        });
    }

    private void assertDespawn(final Character<?> npc) {
        final var despawned = npc.despawn();
        if (despawned) pass("despawn", "despawned NPC");
        else fail("despawn", "failed to despawn NPC");
    }

    private void assertRespawn(final Character<?> npc) {
        final var respawned = npc.respawn();
        if (respawned) pass("respawn", "respawned NPC");
        else fail("respawn", "failed to respawn NPC");
    }

    private void assertIsNPC(final org.bukkit.entity.Entity entity) {
        if (controller.isNPC(entity)) pass("isNPC", "entity is an NPC");
        else fail("isNPC", "entity is not recognized as NPC");
    }
}
