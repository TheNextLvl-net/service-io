package net.thenextlvl.service.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.npc.CharacterController;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiPredicate;

@NullMarked
public class CharacterArgumentType extends WrappedArgumentType<String, CharacterController> {
    public CharacterArgumentType(ServicePlugin plugin, BiPredicate<CommandContext<?>, CharacterController> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.getServer().getServicesManager()
                .getRegistrations(CharacterController.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(type))
                .findAny().orElseThrow(), (context, builder) -> {
            plugin.getServer().getServicesManager()
                    .getRegistrations(CharacterController.class).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .filter(controller -> filter.test(context, controller))
                    .map(CharacterController::getName)
                    .map(StringArgumentType::escapeIfRequired)
                    .filter(name -> name.contains(builder.getRemaining()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
