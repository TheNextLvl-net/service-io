package net.thenextlvl.service.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.economy.EconomyController;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.function.BiPredicate;

public class EconomyArgumentType extends WrappedArgumentType<String, EconomyController> {
    public EconomyArgumentType(ServicePlugin plugin, BiPredicate<CommandContext<?>, EconomyController> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.getServer().getServicesManager()
                .getRegistrations(EconomyController.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(type))
                .findAny().orElseThrow(), (context, builder) -> {
            plugin.getServer().getServicesManager()
                    .getRegistrations(EconomyController.class).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .filter(controller -> filter.test(context, controller))
                    .map(EconomyController::getName)
                    .map(StringArgumentType::escapeIfRequired)
                    .filter(name -> name.contains(context.getInput()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}