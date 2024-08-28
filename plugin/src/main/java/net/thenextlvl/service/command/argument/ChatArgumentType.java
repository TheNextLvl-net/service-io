package net.thenextlvl.service.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.function.BiPredicate;

public class ChatArgumentType extends WrappedArgumentType<String, ChatController> {
    public ChatArgumentType(ServicePlugin plugin, BiPredicate<CommandContext<?>, ChatController> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.getServer().getServicesManager()
                .getRegistrations(ChatController.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(type))
                .findAny().orElseThrow(), (context, builder) -> {
            plugin.getServer().getServicesManager()
                    .getRegistrations(ChatController.class).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .filter(controller -> filter.test(context, controller))
                    .map(ChatController::getName)
                    .map(StringArgumentType::escapeIfRequired)
                    .filter(name -> name.contains(context.getInput()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
