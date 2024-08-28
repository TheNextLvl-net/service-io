package net.thenextlvl.service.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.service.ServicePlugin;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class VaultCommand {
    private final ServicePlugin plugin;

    public void register() {
        var command = Commands.literal("vault")
                .requires(stack -> stack.getSender().hasPermission("service.command"))
                .then(new VaultInfoCommand(plugin).create())
                .then(new VaultConvertCommand(plugin).create())
                .build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(command)));
    }
}
