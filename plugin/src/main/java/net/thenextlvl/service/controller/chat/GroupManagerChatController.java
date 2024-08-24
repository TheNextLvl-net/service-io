package net.thenextlvl.service.controller.chat;

import lombok.Getter;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.model.chat.GroupManagerChatProfile;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GroupManagerChatController implements ChatController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);
    private final @Getter String name = "GroupManager";

    @Override
    public CompletableFuture<ChatProfile> getProfile(OfflinePlayer player) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getProfile(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(OfflinePlayer player, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getProfile(holder, player.getUniqueId(), player.getName());
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(UUID uuid) {
        var holder = groupManager.getWorldsHolder().getDefaultWorld();
        return getProfile(holder, uuid, null);
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(UUID uuid, World world) {
        var holder = groupManager.getWorldsHolder().getWorldData(world.getName());
        return getProfile(holder, uuid, null);
    }

    private CompletableFuture<ChatProfile> getProfile(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return CompletableFuture.completedFuture(null);
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        if (user == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(new GroupManagerChatProfile(user, holder));
    }
}
