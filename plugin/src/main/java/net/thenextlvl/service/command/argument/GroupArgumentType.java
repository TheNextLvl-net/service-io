package net.thenextlvl.service.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.group.GroupController;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiPredicate;

@NullMarked
public class GroupArgumentType extends WrappedArgumentType<String, GroupController> {
    public GroupArgumentType(ServicePlugin plugin, BiPredicate<CommandContext<?>, GroupController> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.getServer().getServicesManager()
                .getRegistrations(GroupController.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(type))
                .findAny().orElseThrow(), (context, builder) -> {
            plugin.getServer().getServicesManager()
                    .getRegistrations(GroupController.class).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .filter(controller -> filter.test(context, controller))
                    .map(GroupController::getName)
                    .map(StringArgumentType::escapeIfRequired)
                    .filter(name -> name.contains(builder.getRemaining()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
