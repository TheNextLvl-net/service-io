package net.thenextlvl.service.providers.groupmanager;

import net.thenextlvl.service.api.Wrappable;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class GroupManagerChatController implements ChatController, Wrappable {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player) {
        return getProfile(player)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, World world) {
        return getProfile(player, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid) {
        return getProfile(uuid)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, World world) {
        return getProfile(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public Optional<ChatProfile> getProfile(OfflinePlayer player) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getProfile(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public Optional<ChatProfile> getProfile(OfflinePlayer player, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getProfile(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getProfile(holder, uuid, null);
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getProfile(holder, uuid, null);
    }

    private Optional<ChatProfile> getProfile(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return Optional.empty();
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        return user != null ? Optional.of(new GroupManagerChatProfile(user, holder)) : Optional.empty();
    }

    @Override
    public Plugin getPlugin() {
        return groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager Chat";
    }

    @Override
    public boolean createWrapper() {
        return false;
    }
}
