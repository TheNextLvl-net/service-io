package net.thenextlvl.services.chat;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.services.ServicePlugin;
import net.thenextlvl.services.api.chat.ChatController;
import net.thenextlvl.services.api.chat.ChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class PaperChatController implements ChatController {
    private final ServicePlugin plugin;

    @Override
    public CompletableFuture<ChatProfile> getProfile(OfflinePlayer player) {
        return CompletableFuture.completedFuture(new PaperChatProfile(player));
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(OfflinePlayer player, World world) {
        return getProfile(player);
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(UUID uuid) {
        return getProfile(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<ChatProfile> getProfile(UUID uuid, World world) {
        return getProfile(uuid);
    }
}
