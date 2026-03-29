package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.chat.ChatController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class ChatConverter extends PlayerConverter<ChatController> {
    public ChatConverter(final Plugin plugin, final ChatController source, final ChatController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert(final OfflinePlayer player) {
        return source.resolveProfile(player).thenAccept(profile -> target.resolveProfile(player)
                .thenAccept(targetProfile -> {
                    profile.getPrefixes().forEach((priority, prefix) -> targetProfile.setPrefix(prefix, priority));
                    profile.getSuffixes().forEach((priority, suffix) -> targetProfile.setSuffix(suffix, priority));
                    profile.getDisplayName().ifPresent(targetProfile::setDisplayName);
                }));
    }
}
