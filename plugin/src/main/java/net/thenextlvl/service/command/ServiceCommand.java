package net.thenextlvl.service.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.service.ServicePlugin;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class ServiceCommand {
    private final ServicePlugin plugin;

    public void register() {
        var command = Commands.literal("service")
                .requires(stack -> stack.getSender().hasPermission("service.command"))
                .then(new ServiceInfoCommand(plugin).create())
                .then(new ServiceConvertCommand(plugin).create())
                .build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(command)));
    }
}
