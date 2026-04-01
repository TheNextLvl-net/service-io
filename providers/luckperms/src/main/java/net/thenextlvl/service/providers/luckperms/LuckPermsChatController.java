package net.thenextlvl.service.providers.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;
import net.thenextlvl.service.api.DoNotWrap;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@DoNotWrap
@NullMarked
public final class LuckPermsChatController implements ChatController {
    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Plugin plugin;

    public LuckPermsChatController(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId(), player.getName()).thenApply(user ->
                new LuckPermsChatProfile(user, QueryOptions.defaultContextualOptions(), null));
    }

    @Override
    public CompletableFuture<ChatProfile> loadProfile(final OfflinePlayer player, final World world) {
        return luckPerms.getUserManager().loadUser(player.getUniqueId()).thenApply(user -> {
            final var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsChatProfile(user, options, world);
        });
    }

    @Override
    public Optional<ChatProfile> getProfile(final OfflinePlayer player) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId())).map(user -> {
            return new LuckPermsChatProfile(user, QueryOptions.defaultContextualOptions(), null);
        });
    }

    @Override
    public Optional<ChatProfile> getProfile(final OfflinePlayer player, final World world) {
        return Optional.ofNullable(luckPerms.getUserManager().getUser(player.getUniqueId())).map(user -> {
            final var options = QueryOptions.contextual(ImmutableContextSet.of("world", world.getName()));
            return new LuckPermsChatProfile(user, options, world);
        });
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return "LuckPerms Chat";
    }
}
