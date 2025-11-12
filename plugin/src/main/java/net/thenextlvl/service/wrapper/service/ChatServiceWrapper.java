package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault.chat.Chat;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.WrappedChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class ChatServiceWrapper implements ChatController, Wrapper {
    private final Plugin provider;
    private final Chat chat;

    public ChatServiceWrapper(Chat chat, Plugin provider) {
        this.chat = chat;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(OfflinePlayer player, @Nullable World world) {
        return CompletableFuture.completedFuture(new WrappedChatProfile(world, chat, player));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, @Nullable World world) {
        return loadProfile(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public Optional<ChatProfile> getProfile(OfflinePlayer player, @Nullable World world) {
        return Optional.of(new WrappedChatProfile(world, chat, player));
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid, @Nullable World world) {
        return getProfile(provider.getServer().getOfflinePlayer(uuid), world);
    }

    @Override
    public Plugin getPlugin() {
        return provider;
    }

    @Override
    public String getName() {
        return chat.getName() + " Wrapper";
    }
}
