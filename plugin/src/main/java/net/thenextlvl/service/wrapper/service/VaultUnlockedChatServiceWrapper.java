package net.thenextlvl.service.wrapper.service;

import net.milkbowl.vault2.chat.Chat;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.model.VaultUnlockedChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class VaultUnlockedChatServiceWrapper implements ChatController, Wrapper {
    private final Plugin provider;
    private final Chat chat;

    public VaultUnlockedChatServiceWrapper(final Chat chat, final Plugin provider) {
        this.chat = chat;
        this.provider = provider;
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player) {
        return loadProfile(player, null);
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player, @Nullable final World world) {
        return CompletableFuture.completedFuture(new VaultUnlockedChatProfile(world, chat, player));
    }

    @Override
    public Optional<ChatProfile> getProfile(final OfflinePlayer player) {
        return Optional.of(new VaultUnlockedChatProfile(null, chat, player));
    }

    @Override
    public Optional<ChatProfile> getProfile(final OfflinePlayer player, @Nullable final World world) {
        return Optional.of(new VaultUnlockedChatProfile(world, chat, player));
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
