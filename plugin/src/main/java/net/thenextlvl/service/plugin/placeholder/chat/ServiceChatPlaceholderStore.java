package net.thenextlvl.service.plugin.placeholder.chat;

import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.model.Display;
import net.thenextlvl.service.plugin.placeholder.api.PlaceholderStore;
import org.bukkit.plugin.Plugin;

public final class ServiceChatPlaceholderStore extends PlaceholderStore<ChatController> {
    public ServiceChatPlaceholderStore(final Plugin plugin) {
        super(plugin, ChatController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_prefix%
        registerResolver("prefix", (provider, player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getPrefix).orElse("");
        });

        // %serviceio_suffix%
        registerResolver("suffix", (provider, player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getSuffix).orElse("");
        });

        // %serviceio_displayname%
        registerResolver("displayname", (provider, player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getDisplayName).orElse(player.getName());
        });
    }
}
