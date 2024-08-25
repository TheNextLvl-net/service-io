package net.thenextlvl.service.controller.chat;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.model.chat.LuckPermsChatProfile;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsChatController implements ChatController {
    private final @Getter String name = "LuckPerms";
    private final LuckPerms luckPerms = LuckPermsProvider.get();

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user ->
                new LuckPermsChatProfile(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, World world) {
        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {
            var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsChatProfile(user, options);
        });
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user ->
                new LuckPermsChatProfile(user, QueryOptions.defaultContextualOptions()));
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid, World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(uuid)).map(user -> {
            var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsChatProfile(user, options);
        });
    }
}
