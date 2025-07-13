package net.thenextlvl.service.placeholder.chat;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.model.Display;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ServiceChatPlaceholderStore extends PlaceholderStore<ChatController> {
    public ServiceChatPlaceholderStore(ServicePlugin plugin) {
        super(plugin, ChatController.class);
    }

    @Override
    protected void registerResolvers(ChatController provider) {
        // %serviceio_prefix%
        registerResolver("prefix", (player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getPrefix).orElse("");
        });

        // %serviceio_suffix%
        registerResolver("suffix", (player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getSuffix).orElse("");
        });

        // %serviceio_displayname%
        registerResolver("displayname", (player, matcher) -> {
            return provider.getProfile(player).flatMap(Display::getDisplayName).orElse(player.getName());
        });
    }
}
