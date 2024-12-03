package net.thenextlvl.service.wrapper.service;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.chat.Chat;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.wrapper.service.model.WrappedChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
@RequiredArgsConstructor
public class ChatServiceWrapper implements ChatController {
    private final Chat chat;
    private final ServicePlugin plugin;

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player) {
        return getProfile(player).map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, World world) {
        return getProfile(player, world).map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid) {
        return getProfile(uuid).map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, World world) {
        return getProfile(uuid, world).map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public Optional<ChatProfile> getProfile(OfflinePlayer player) {
        return Optional.of(new WrappedChatProfile(null, chat, player));
    }

    @Override
    public Optional<ChatProfile> getProfile(OfflinePlayer player, World world) {
        return Optional.of(new WrappedChatProfile(world, chat, player));
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid) {
        return getProfile(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid, World world) {
        return getProfile(plugin.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public String getName() {
        return chat.getName();
    }
}
