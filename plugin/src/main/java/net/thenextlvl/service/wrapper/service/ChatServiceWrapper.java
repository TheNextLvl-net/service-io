package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.chat.Chat;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.wrapper.service.model.WrappedChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class ChatServiceWrapper implements ChatController {
    private final Plugin provider;
    private final ServicePlugin plugin;
    private final Chat chat;

    public ChatServiceWrapper(Chat chat, Plugin provider, ServicePlugin plugin) {
        this.chat = chat;
        this.plugin = plugin;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> new WrappedChatProfile(null, chat, player));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, World world) {
        return CompletableFuture.supplyAsync(() -> new WrappedChatProfile(world, chat, player));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid) {
        return loadProfile(plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, World world) {
        return loadProfile(plugin.getServer().getOfflinePlayer(uuid), world);
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
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return chat.getName();
    }
}
